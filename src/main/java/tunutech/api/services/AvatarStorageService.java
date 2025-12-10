package tunutech.api.services;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class AvatarStorageService {

    // Utilisez la même configuration que DocumentService
    @Value("${app.storage.local.windows-path:D:/Engenniering/WEB/Projets/projet_traduction_trilangue/api/documents/}")
    private String windowsPath;

    @Value("${app.storage.local.linux-path:./uploads/documents}")
    private String linuxPath;

    @Value("${app.storage.max-file-size:10485760}")
    private long maxFileSize;

    private static final String AVATAR_SUBDIRECTORY = "avatars";
    private String actualStoragePath;
    private Path avatarDirectory;
    public String actualStoragePathforUse;

    @PostConstruct
    public void init() {
        // Utilisez la même logique de détection que DocumentService
        actualStoragePath = getActualStoragePath();
        actualStoragePathforUse=getActualStoragePath();
        avatarDirectory = Paths.get(actualStoragePath, AVATAR_SUBDIRECTORY);

        try {
            Files.createDirectories(avatarDirectory);
            log.info("Répertoire avatars initialisé: {}", avatarDirectory.toAbsolutePath());
        } catch (IOException e) {
            log.error("Impossible de créer le répertoire avatars", e);
            throw new RuntimeException("Erreur d'initialisation du stockage avatars", e);
        }
    }

    /**
     * Même méthode que DocumentService pour la compatibilité
     */
    private String getActualStoragePath() {
        String os = System.getProperty("os.name").toLowerCase();

        if (os.contains("win")) {
            log.info("Environnement Windows détecté - Stockage avatars: {}", windowsPath);
            return windowsPath;
        } else {
            log.info("Environnement Linux détecté - Stockage avatars: {}", linuxPath);

            // Ajouter la détection Render comme dans DocumentService
            if (isRunningOnRender()) {
                // Sur Render, utiliser /tmp/ pour la compatibilité
                String renderPath = "/tmp/documents/";
                log.info("Render détecté - Stockage avatars: {}", renderPath);
                return renderPath;
            }
            return linuxPath;
        }
    }

    private boolean isRunningOnRender() {
        return System.getenv("RENDER") != null
                || System.getenv("RENDER_EXTERNAL_HOSTNAME") != null
                || System.getenv("RENDER_SERVICE_ID") != null;
    }

    public String uploadAvatar(MultipartFile avatarFile, Long userId) {
        try {
            validateAvatarFile(avatarFile);

            // Générer un nom de fichier unique avec userId
            String originalFilename = avatarFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = generateAvatarFilename(userId, fileExtension);

            // Sauvegarder le fichier
            Path filePath = avatarDirectory.resolve(uniqueFilename);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("Avatar sauvegardé: {} -> {} ({} bytes)",
                    originalFilename, filePath, avatarFile.getSize());

            // Retourner seulement le nom du fichier (pas le chemin complet)
            return uniqueFilename;

        } catch (IOException e) {
            log.error("Erreur lors de l'upload de l'avatar", e);
            throw new RuntimeException("Erreur lors de l'upload de l'avatar: " + e.getMessage(), e);
        }
    }

    public String uploadAvatar(MultipartFile avatarFile) {
        // Version sans userId pour la rétrocompatibilité
        return uploadAvatar(avatarFile, null);
    }

    public void deleteAvatar(String filename) {
        try {
            if (filename != null && !filename.isEmpty()) {
                Path filePath = avatarDirectory.resolve(filename);
                boolean deleted = Files.deleteIfExists(filePath);

                if (deleted) {
                    log.info("Avatar supprimé: {}", filePath);
                } else {
                    log.warn("Avatar non trouvé: {}", filename);
                }
            }
        } catch (IOException e) {
            log.error("Erreur lors de la suppression de l'avatar: {}", filename, e);
            throw new RuntimeException("Erreur lors de la suppression de l'avatar", e);
        }
    }

    public Path getAvatarPath(String filename) {
        return avatarDirectory.resolve(filename);
    }

    public Resource getAvatarAsResource(String filename) {
        try {
            Path filePath = getAvatarPath(filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("Avatar non trouvé ou illisible: {}", filename);
                return null;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de l'avatar: {}", filename, e);
            return null;
        }
    }

    /**
     * Validation du fichier avatar
     */
    private void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier avatar est vide");
        }

        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                    String.format("Avatar trop volumineux: %d bytes (max: %d)",
                            file.getSize(), maxFileSize));
        }

        // Vérifier le type MIME
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Seules les images sont autorisées");
        }

        // Vérifier l'extension
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !isValidImageExtension(originalFilename)) {
            throw new IllegalArgumentException("Format d'image non supporté");
        }
    }

    /**
     * Génère un nom de fichier unique pour l'avatar
     */
    private String generateAvatarFilename(Long userId, String extension) {
        String timestamp = String.valueOf(System.currentTimeMillis());
        String randomId = UUID.randomUUID().toString().substring(0, 8);
        String userIdPart = userId != null ? "user_" + userId + "_" : "";

        return String.format("%s%s_%s%s",
                userIdPart, timestamp, randomId,
                extension.isEmpty() ? ".jpg" : extension);
    }

    /**
     * Extrait l'extension d'un fichier
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf(".")).toLowerCase();
    }

    /**
     * Vérifie si l'extension est une image valide
     */
    private boolean isValidImageExtension(String filename) {
        String extension = getFileExtension(filename).toLowerCase();
        List<String> allowedExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");
        return allowedExtensions.contains(extension);
    }

    /**
     * Pour la compatibilité avec l'ancien code
     */
    public void deleteAvatarByUrl(String avatarUrl) {
        if (avatarUrl != null && avatarUrl.contains("/")) {
            String filename = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
            deleteAvatar(filename);
        }
    }

    /**
     * Construit l'URL d'accès web de l'avatar
     */
    public String buildAvatarUrl(String filename) {
        if (filename == null || filename.isEmpty()) {
            return null;
        }
        return "/api/avatars/" + filename;
    }

    /**
     * Extrait le nom de fichier depuis une URL
     */
    public String extractFilenameFromUrl(String avatarUrl) {
        if (avatarUrl == null || !avatarUrl.contains("/")) {
            return null;
        }
        return avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
    }
}