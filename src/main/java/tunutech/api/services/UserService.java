package tunutech.api.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tunutech.api.Utils.Functions;
import tunutech.api.dtos.UserDto;
import tunutech.api.dtos.UserResponseDTO;
import tunutech.api.model.AvatarType;
import tunutech.api.model.User;
import tunutech.api.repositories.UserRepository;

import java.security.NoSuchAlgorithmException;
import java.util.*;

@Service
public class UserService {
    private final UserRepository userRepository;
    private Functions functions;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> allUsers() {
        List<User> users = new ArrayList<>();

        userRepository.findAll().forEach(users::add);

        return users;
    }
    public UserDto getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        System.out.println("🔎 Authentication = " + authentication);

        if (authentication == null) {
            return null;
        }
        System.out.println("🔎 Principal = " + authentication.getPrincipal());

        if (!(authentication.getPrincipal() instanceof User)) {
            return null;
        }
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Utilisateur non authentifié");
        }

        String email = authentication.getName();

        User user = (User) authentication.getPrincipal();

        return new UserDto(
                user.getId(),
                user.getEmail()
        );
    }

    public User getUnique(Long id) throws Exception {
        Optional<User>user=userRepository.findById(Math.toIntExact(id));
        if(user.isPresent())
        {
            return user.get();
        }throw new Exception("User not found");
    }
    public User getByIdByForce(long id)
    {
        return userRepository.findById((int) id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }
    public User getByEmailByForce(String email)
    {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public Optional<User> getByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getById(Long id)
    {
        return userRepository.findById(id.intValue());
    }

    public User getByTraducteur(Long id)
    {
        return userRepository.findByTraducteurId(id)
                .orElseThrow(() -> new RuntimeException("User not found with"));
    }

    public User getByClient(Long id)
    {
        return userRepository.findByClientId(id)
                .orElseThrow(() -> new RuntimeException("User not found with"));
    }

    public User setEnabled(Long id, boolean isenable) throws Exception {
        User user=this.getUnique(id);
        user.setActive(isenable);
        return userRepository.save(user);
    }

    public User setPresent(Long id, boolean ispresent) throws Exception {
        User user=this.getUnique(id);
        user.setPresent(ispresent);
        return userRepository.save(user);
    }


    @Autowired
    private AvatarStorageService avatarStorageService;

    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );

    public UserResponseDTO uploadUserAvatar(Long userId, MultipartFile avatarFile) throws NoSuchAlgorithmException {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Validation du type de fichier
        if (!ALLOWED_IMAGE_TYPES.contains(avatarFile.getContentType())) {
            throw new RuntimeException("Type de fichier non autorisé. Types acceptés: " + ALLOWED_IMAGE_TYPES);
        }

        // Validation de la taille (utilise votre configuration max-file-size)
        if (avatarFile.getSize() > 10 * 1024 * 1024) { // 10MB
            throw new RuntimeException("Fichier trop volumineux. Taille max: 10MB");
        }

        // Supprimer l'ancien avatar s'il existe
        if (user.getAvatarUrl() != null && user.getAvatarType() == AvatarType.UPLOAD) {
            avatarStorageService.deleteAvatar(user.getAvatarUrl());
        }

        // Upload du nouveau avatar
        String avatarUrl = avatarStorageService.uploadAvatar(avatarFile);

        // Mise à jour de l'utilisateur
        user.setAvatarUrl(avatarUrl);
        user.setAvatarType(AvatarType.UPLOAD);

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    public UserResponseDTO setInitialsAvatar(Long userId, String color) throws NoSuchAlgorithmException {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Supprimer l'image uploadée si elle existe
        if (user.getAvatarType() == AvatarType.UPLOAD && user.getAvatarUrl() != null) {
            avatarStorageService.deleteAvatar(user.getAvatarUrl());
        }

        user.setAvatarType(AvatarType.INITIALS);
        user.setAvatarInitialColor(color != null ? color : generateRandomColor());
        user.setAvatarUrl(null);

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    public UserResponseDTO removeAvatar(Long userId) throws NoSuchAlgorithmException {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Supprimer le fichier physique si c'est une image uploadée
        if (user.getAvatarType() == AvatarType.UPLOAD && user.getAvatarUrl() != null) {
            avatarStorageService.deleteAvatar(user.getAvatarUrl());
        }

        // Réinitialiser à l'avatar par défaut (initiales)
        user.setAvatarType(AvatarType.INITIALS);
        user.setAvatarUrl(null);
        user.setAvatarInitialColor(generateRandomColor());

        User savedUser = userRepository.save(user);
        return UserResponseDTO.fromEntity(savedUser);
    }

    private String generateRandomColor() {
        String[] colors = {
                "#667eea", "#764ba2", "#f093fb", "#f5576c", "#4facfe",
                "#00f2fe", "#43e97b", "#38f9d7", "#fa709a", "#fee140",
                "#a8edea", "#fed6e3", "#ffecd2", "#fcb69f"
        };
        return colors[(int) (Math.random() * colors.length)];
    }
}
