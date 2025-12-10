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

    // Chemins configurés dans application.properties
    @Value("${app.storage.local.windows-path:D:/Engenniering/WEB/Projets/projet_traduction_trilangue/api/documents/}")
    private String windowsPath;

    @Value("${app.storage.local.linux-path:${STORAGE_PATH:./uploads}/documents}")
    private String linuxPath;

    @Value("${app.storage.max-file-size:10485760}")
    private long maxFileSize;

    private static final String AVATAR_SUBDIRECTORY = "avatars";
    private Path avatarDirectory;

    public String actualStoragePathforUse;

    @PostConstruct
    public void init() {
        try {
            // Obtenir le chemin de stockage adapté à l'environnement
            String storagePath = getActualStoragePath();
            avatarDirectory = Paths.get(storagePath, AVATAR_SUBDIRECTORY);

            // Créer le répertoire (et les parents si nécessaire)
            Files.createDirectories(avatarDirectory);
                actualStoragePathforUse=getActualStoragePath();

            log.info("✅ Répertoire avatars initialisé: {}", avatarDirectory.toAbsolutePath());
            log.info("✅ Chemin disponible en écriture: {}", Files.isWritable(avatarDirectory));

            // Vérifier les permissions
            ensureDirectoryPermissions();

        } catch (IOException e) {
            log.error("❌ Erreur lors de l'initialisation du stockage: {}", e.getMessage());

            // Tentative de fallback sur /tmp/ pour les environnements cloud
            tryFallbackStorage();
        }
    }

    /**
     * Méthode de secours si le chemin configuré ne fonctionne pas
     */
    private void tryFallbackStorage() {
        try {
            // Sur les environnements cloud, /tmp/ est toujours disponible
            if (isRunningOnCloud()) {
                avatarDirectory = Paths.get("/tmp", AVATAR_SUBDIRECTORY);
                Files.createDirectories(avatarDirectory);
                log.info("✅ Stockage de secours initialisé: {}", avatarDirectory.toAbsolutePath());
            } else {
                // En local, essayer le répertoire courant
                avatarDirectory = Paths.get(".", "uploads", AVATAR_SUBDIRECTORY);
                Files.createDirectories(avatarDirectory);
                log.info("✅ Stockage local de secours initialisé: {}", avatarDirectory.toAbsolutePath());
            }
        } catch (IOException e2) {
            log.error("❌ Échec du stockage de secours", e2);
            throw new RuntimeException("Impossible d'initialiser le stockage des avatars", e2);
        }
    }

    /**
     * Vérifie et corrige les permissions du répertoire si nécessaire
     */
    private void ensureDirectoryPermissions() {
        try {
            // S'assurer que le répertoire est accessible en écriture
            if (!Files.isWritable(avatarDirectory)) {
                log.warn("⚠️  Le répertoire n'est pas accessible en écriture, tentative de correction...");

                // Sur Linux/Unix, essayer de changer les permissions
                if (!System.getProperty("os.name").toLowerCase().contains("win")) {
                    Process process = Runtime.getRuntime().exec(
                            new String[]{"chmod", "755", avatarDirectory.toAbsolutePath().toString()}
                    );
                    process.waitFor();
                    log.info("✅ Permissions ajustées pour: {}", avatarDirectory);
                }
            }
        } catch (Exception e) {
            log.warn("⚠️  Impossible d'ajuster les permissions: {}", e.getMessage());
        }
    }

    /**
     * Détermine le chemin de stockage en fonction de l'environnement
     */
    private String getActualStoragePath() {
        String os = System.getProperty("os.name").toLowerCase();

        // 1. Vérifier d'abord la variable d'environnement STORAGE_PATH (priorité)
        String envStoragePath = System.getenv("STORAGE_PATH");
        if (envStoragePath != null && !envStoragePath.trim().isEmpty()) {
            log.info("📁 Utilisation du chemin d'environnement STORAGE_PATH: {}", envStoragePath);
            return Paths.get(envStoragePath, "documents").toString();
        }

        // 2. Détection automatique basée sur l'OS et l'environnement
        if (os.contains("win")) {
            log.info("🪟 Environnement Windows détecté");
            log.info("📂 Chemin Windows: {}", windowsPath);
            return windowsPath;
        } else {
            // Environnement Linux/Unix
            if (isRunningOnCloud()) {
                log.info("☁️  Environnement Cloud détecté (Render/Heroku/etc.)");
                // Sur le cloud, utiliser /tmp/ qui est toujours disponible
                return "/tmp/documents";
            } else {
                log.info("🐧 Environnement Linux local détecté");
                log.info("📂 Chemin Linux: {}", linuxPath);
                return linuxPath;
            }
        }
    }

    /**
     * Vérifie si l'application tourne sur un environnement cloud
     */
    private boolean isRunningOnCloud() {
        // Render
        if (System.getenv("RENDER") != null ||
                System.getenv("RENDER_EXTERNAL_HOSTNAME") != null) {
            log.debug("📍 Environnement Render détecté");
            return true;
        }

        // Heroku
        if (System.getenv("DYNO") != null) {
            log.debug("📍 Environnement Heroku détecté");
            return true;
        }

        // Railway
        if (System.getenv("RAILWAY_ENVIRONMENT") != null) {
            log.debug("📍 Environnement Railway détecté");
            return true;
        }

        // Vercel
        if (System.getenv("VERCEL") != null) {
            log.debug("📍 Environnement Vercel détecté");
            return true;
        }

        // Google Cloud Run
        if (System.getenv("K_SERVICE") != null) {
            log.debug("📍 Environnement Google Cloud Run détecté");
            return true;
        }

        // AWS Lambda
        if (System.getenv("AWS_LAMBDA_FUNCTION_NAME") != null) {
            log.debug("📍 Environnement AWS Lambda détecté");
            return true;
        }

        return false;
    }

    public String uploadAvatar(MultipartFile avatarFile, Long userId) {
        try {
            validateAvatarFile(avatarFile);

            String originalFilename = avatarFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String uniqueFilename = generateAvatarFilename(userId, fileExtension);

            Path filePath = avatarDirectory.resolve(uniqueFilename);
            Files.copy(avatarFile.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            log.info("✅ Avatar sauvegardé: {} -> {} ({} bytes)",
                    originalFilename, filePath, avatarFile.getSize());

            // Vérifier que le fichier a bien été écrit
            if (!Files.exists(filePath) || Files.size(filePath) == 0) {
                throw new IOException("Le fichier n'a pas été correctement sauvegardé");
            }

            return uniqueFilename;

        } catch (IOException e) {
            log.error("❌ Erreur lors de l'upload de l'avatar", e);
            throw new RuntimeException("Erreur lors de l'upload de l'avatar: " + e.getMessage(), e);
        }
    }

    public String uploadAvatar(MultipartFile avatarFile) {
        return uploadAvatar(avatarFile, null);
    }

    public void deleteAvatar(String filename) {
        try {
            if (filename != null && !filename.isEmpty()) {
                Path filePath = avatarDirectory.resolve(filename);
                boolean deleted = Files.deleteIfExists(filePath);

                if (deleted) {
                    log.info("🗑️  Avatar supprimé: {}", filePath);
                } else {
                    log.warn("⚠️  Avatar non trouvé pour suppression: {}", filename);
                }
            }
        } catch (IOException e) {
            log.error("❌ Erreur lors de la suppression de l'avatar: {}", filename, e);
            throw new RuntimeException("Erreur lors de la suppression de l'avatar", e);
        }
    }

    public Path getAvatarPath(String filename) {
        return avatarDirectory.resolve(filename);
    }

    public Resource getAvatarAsResource(String filename) {
        try {
            Path filePath = getAvatarPath(filename);

            if (!Files.exists(filePath)) {
                log.warn("⚠️  Avatar non trouvé: {}", filename);
                return null;
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                log.warn("⚠️  Avatar illisible: {}", filename);
                return null;
            }
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération de l'avatar: {}", filename, e);
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

        // Nettoyer l'extension
        String cleanExtension = extension.toLowerCase();
        if (!cleanExtension.startsWith(".")) {
            cleanExtension = "." + cleanExtension;
        }

        // Liste des extensions d'image valides
        List<String> validExtensions = Arrays.asList(".jpg", ".jpeg", ".png", ".gif", ".webp", ".bmp");
        if (!validExtensions.contains(cleanExtension)) {
            cleanExtension = ".jpg"; // Extension par défaut
        }

        return String.format("%s%s_%s%s",
                userIdPart, timestamp, randomId, cleanExtension);
    }

    /**
     * Extrait l'extension d'un fichier
     */
    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
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

    /**
     * Méthode utilitaire pour vérifier l'état du stockage
     */
    public StorageInfo getStorageInfo() {
        return new StorageInfo(
                avatarDirectory != null ? avatarDirectory.toAbsolutePath().toString() : "Non initialisé",
                avatarDirectory != null && Files.exists(avatarDirectory),
                avatarDirectory != null && Files.isWritable(avatarDirectory),
                isRunningOnCloud()
        );
    }

    /**
     * Classe pour retourner des informations sur le stockage
     */
    public static class StorageInfo {
        private final String path;
        private final boolean exists;
        private final boolean writable;
        private final boolean cloudEnvironment;

        public StorageInfo(String path, boolean exists, boolean writable, boolean cloudEnvironment) {
            this.path = path;
            this.exists = exists;
            this.writable = writable;
            this.cloudEnvironment = cloudEnvironment;
        }

        // Getters
        public String getPath() { return path; }
        public boolean isExists() { return exists; }
        public boolean isWritable() { return writable; }
        public boolean isCloudEnvironment() { return cloudEnvironment; }

        @Override
        public String toString() {
            return String.format("StorageInfo{path='%s', exists=%s, writable=%s, cloud=%s}",
                    path, exists, writable, cloudEnvironment);
        }
    }
}