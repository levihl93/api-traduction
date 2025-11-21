package tunutech.api.services;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class AvatarStorageService {
    @Value("${app.storage.root-path}")
    private String storageRootPath;

    @Value("${file.upload-directory}")
    private String uploadDir;

    private static final String AVATAR_SUBDIRECTORY = "avatars";

    public String uploadAvatar(MultipartFile avatarFile) {
        try {
            // Utiliser le répertoire de stockage principal + sous-dossier avatars
            Path avatarDirectory = Paths.get(storageRootPath, AVATAR_SUBDIRECTORY);

            // Créer le dossier s'il n'existe pas
            if (!Files.exists(avatarDirectory)) {
                Files.createDirectories(avatarDirectory);
            }

            // Générer un nom de fichier unique
            String originalFilename = avatarFile.getOriginalFilename();
            String fileExtension = getFileExtension(originalFilename);
            String filename = UUID.randomUUID() + fileExtension;

            // Sauvegarder le fichier
            Path filePath = avatarDirectory.resolve(filename);
            Files.copy(avatarFile.getInputStream(), filePath);

            // Retourner le chemin relatif pour l'accès web
            return "/" + AVATAR_SUBDIRECTORY + "/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de l'upload de l'avatar: " + e.getMessage(), e);
        }
    }

    public void deleteAvatar(String avatarUrl) {
        try {
            if (avatarUrl != null && avatarUrl.startsWith("/" + AVATAR_SUBDIRECTORY + "/")) {
                String filename = avatarUrl.substring(avatarUrl.lastIndexOf("/") + 1);
                Path filePath = Paths.get(storageRootPath, AVATAR_SUBDIRECTORY, filename);
                Files.deleteIfExists(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors de la suppression de l'avatar", e);
        }
    }

    private String getFileExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            return ".jpg"; // Extension par défaut
        }
        return filename.substring(filename.lastIndexOf("."));
    }
}
