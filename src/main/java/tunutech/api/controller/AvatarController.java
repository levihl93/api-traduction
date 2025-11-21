package tunutech.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tunutech.api.dtos.UserResponseDTO;
import tunutech.api.services.UserService;
import org.springframework.beans.factory.annotation.Value;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/api/avatars")
@RequiredArgsConstructor

public class AvatarController {
    private final UserService userService;
    @Value("${file.upload-directory}")
    private String uploadDir;

    @PostMapping("/users/{userId}/upload")
    public ResponseEntity<UserResponseDTO> uploadAvatar(
            @PathVariable Long userId,
            @RequestParam("avatar") MultipartFile avatarFile) throws NoSuchAlgorithmException {

        UserResponseDTO user = userService.uploadUserAvatar(userId, avatarFile);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/{filename}")
    public ResponseEntity<Resource> getAvatar(@PathVariable String filename) {
        try {
            Path filePath = Paths.get(uploadDir, "avatars", filename);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .body(resource);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.status(403).build();
        }
    }
    @PutMapping("/users/{userId}/initials")
    public ResponseEntity<UserResponseDTO> setInitialsAvatar(
            @PathVariable Long userId,
            @RequestParam(value = "color", required = false) String color) throws NoSuchAlgorithmException {

        UserResponseDTO user = userService.setInitialsAvatar(userId, color);
        return ResponseEntity.ok(user);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<UserResponseDTO> removeAvatar(@PathVariable Long userId) throws NoSuchAlgorithmException {
        UserResponseDTO user = userService.removeAvatar(userId);
        return ResponseEntity.ok(user);
    }
}
