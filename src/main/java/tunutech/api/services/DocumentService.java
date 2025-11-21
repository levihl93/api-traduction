package tunutech.api.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tunutech.api.exception.DocumentStorageException;
import tunutech.api.exception.DocumentValidationException;
import tunutech.api.model.Document;
import tunutech.api.model.DocumentStatus;
import tunutech.api.model.Project;
import tunutech.api.repositories.DocumentRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
@Slf4j
public class DocumentService {
    @Value("${app.storage.root-path:./documents}")  // ← Maintenant ça fonctionne
    private String rootStoragePath;

    @Value("${app.storage.max-file-size:10485760}") // 10MB par défaut
    private long maxFileSize;

    @Autowired
    private DocumentRepository documentRepository;

    /**
     * Enregistre un document sur le serveur
     */
    public Document storeDocument(MultipartFile file, Project project,
                                   String category,Float wordscount) {

        // Validation du fichier
        validateFile(file);

        try {
            // 1. Préparer la structure de dossiers
            Path filePath = prepareStoragePath(project.getCode(), category, file.getOriginalFilename());

            // 2. Sauvegarder le fichier physique
            saveFileToDisk(file, filePath);

            // 3. Enregistrer en base de données
            Document document = saveDocumentMetadata(file, filePath, project, category,wordscount);

            log.info("Document enregistré: {} pour le client {}",
                    file.getOriginalFilename(), project.getCode());

            return document;

        } catch (IOException e) {
            log.error("Erreur lors de l'enregistrement du document", e);
            throw new DocumentStorageException("Erreur stockage document: " + e.getMessage());
        }
    }

    /**
     * Validation du fichier
     */
    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new DocumentValidationException("Le fichier est vide");
        }

        if (file.getSize() > maxFileSize) {
            throw new DocumentValidationException(
                    String.format("Fichier trop volumineux: %d bytes (max: %d)",
                            file.getSize(), maxFileSize));
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.contains("..")) {
            throw new DocumentValidationException("Nom de fichier invalide");
        }

        // Validation extension
        if (!isValidFileExtension(originalFilename)) {
            throw new DocumentValidationException("Type de fichier non autorisé");
        }
    }

    /**
     * Prépare le chemin de stockage
     */
    private Path prepareStoragePath(String projectCode, String category, String originalFilename) {
        // Structure: /racine/clients/{client}/{category}/année/mois/jour/
        LocalDate now = LocalDate.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String day = String.format("%02d", now.getDayOfMonth());

        Path storagePath = Paths.get(rootStoragePath,
                "projets", projectCode, category, year, month, day);

        try {
            Files.createDirectories(storagePath);
            log.debug("Dossier créé: {}", storagePath);
        } catch (IOException e) {
            throw new DocumentStorageException("Impossible de créer le dossier: " + storagePath);
        }

        // Générer nom de fichier unique
        String uniqueFilename = generateUniqueFilename(originalFilename);
        return storagePath.resolve(uniqueFilename);
    }

    /**
     * Sauvegarde physique du fichier
     */
    private void saveFileToDisk(MultipartFile file, Path filePath) throws IOException {
        // Copie avec vérification
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Vérifier que le fichier a bien été écrit
        if (!Files.exists(filePath) || Files.size(filePath) == 0) {
            throw new DocumentStorageException("Échec de l'écriture du fichier");
        }

        log.debug("Fichier sauvegardé: {} ({} bytes)",
                filePath, Files.size(filePath));
    }

    /**
     * Enregistrement des métadonnées en BDD
     */
    private Document saveDocumentMetadata(MultipartFile file, Path filePath,
                                          Project project,  String category, Float wordscount) {
        Document document = new Document();
        document.setOriginalName(file.getOriginalFilename());
        document.setStoredName(filePath.getFileName().toString());
        document.setFilePath(filePath.toString());
        document.setFileSize(file.getSize());
        document.setContentType(file.getContentType());
        document.setProject(project);
        document.setTypeDocument(project.getTypeDocument());
        document.setWordsCount(wordscount);
        document.setStatus(DocumentStatus.UPLOADED);
        document.setUploadDate(LocalDateTime.now());
        document.setChecksum(calculateChecksum(file));

        return documentRepository.save(document);
    }

    /**
     * Génère un nom de fichier unique
     */
    private String generateUniqueFilename(String originalFilename) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        String fileExtension = getFileExtension(originalFilename);

        return String.format("%s_%s_%s%s",
                timestamp, randomId,
                sanitizeFilename(originalFilename.replace("." + fileExtension, "")),
                fileExtension.isEmpty() ? "" : "." + fileExtension);
    }

    /**
     * Calcule le checksum MD5 du fichier
     */
    private String calculateChecksum(MultipartFile file) {
        try {
            byte[] data = file.getBytes();
            byte[] hash = MessageDigest.getInstance("MD5").digest(data);
            return bytesToHex(hash);
        } catch (Exception e) {
            log.warn("Impossible de calculer le checksum", e);
            return "unknown";
        }
    }

    // Méthodes utilitaires
    private String getFileExtension(String filename) {
        return Optional.ofNullable(filename)
                .filter(f -> f.contains("."))
                .map(f -> f.substring(filename.lastIndexOf(".") + 1))
                .orElse("");
    }

    private boolean isValidFileExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        return Arrays.asList("pdf", "docx", "doc", "xlsx", "txt", "pptx", "odt")
                .contains(extension);
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9.-]", "_");
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    public Document getUniquebyIdProject(Long idproject)
    {
        return  documentRepository.findByProjectId(idproject);
    }
    public Optional<Document> getUniquebyId(Long iddoc)
    {
        return  documentRepository.findById(iddoc);
    }
    public List<Document> ListbyIdProject(Long idproject)
    {
        return  documentRepository.findDocumentsByProjectId(idproject);
    }

    public Optional<Document> findById(Long id) {
        return documentRepository.findById(id);
    }

    @Value("${file.upload-dir}")
    private String uploadDir;

    public Resource downloadDocument(Long documentId) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new RuntimeException("Document non trouvé"));

        try {
            String cheminRelatif = document.getFilePath();

            // Nettoyer le chemin
            if (cheminRelatif.startsWith(".\\")) {
                cheminRelatif = cheminRelatif.substring(2);
            }

            // Afficher pour debug
            System.out.println("Upload dir: " + uploadDir);
            System.out.println("Chemin relatif: " + cheminRelatif);

            // SOLUTION GARANTIE : Utiliser Paths.get() correctement
            Path basePath = Paths.get(uploadDir);
            Path relativePath = Paths.get(cheminRelatif);
            Path cheminComplet = basePath.resolve(relativePath).normalize();

            System.out.println("Chemin complet: " + cheminComplet);
            System.out.println("Chemin absolu: " + cheminComplet.toAbsolutePath());

            // Vérifier si le fichier existe
            if (!Files.exists(cheminComplet)) {
                System.out.println("FICHIER N'EXISTE PAS à cet emplacement!");
                // Liste les fichiers dans le dossier parent pour debug
                Path parentDir = cheminComplet.getParent();
                if (Files.exists(parentDir)) {
                    System.out.println("Fichiers dans " + parentDir + ":");
                    Files.list(parentDir).forEach(file -> System.out.println(" - " + file.getFileName()));
                }
            }

            Resource resource = new UrlResource(cheminComplet.toUri());

            if (resource.exists()) {
                return resource;
            } else {
                throw new RuntimeException("Fichier non trouvé: " + cheminComplet);
            }

        } catch (Exception e) {
            System.out.println("ERREUR DETAIL: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Erreur: " + e.getMessage());
        }
    }

    public void deleteDocOnBdd(Project project)
    {
        documentRepository.deleteByProject(project);
    }
}
