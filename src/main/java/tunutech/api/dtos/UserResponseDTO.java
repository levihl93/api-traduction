package tunutech.api.dtos;

import lombok.Data;
import tunutech.api.model.AvatarType;
import tunutech.api.model.User;

import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Data
public class UserResponseDTO {
    private Long id;
    private String email;
    private String fullName;
    private String avatarUrl;
    private AvatarType avatarType;
    private String avatarInitialColor;
    private String initials;
    private Date createdAt;

    public static UserResponseDTO fromEntity(User user) throws NoSuchAlgorithmException {
        UserResponseDTO dto = new UserResponseDTO();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setFullName(user.getFullName());
        dto.setAvatarUrl(user.getAvatarDisplayUrl());
        dto.setAvatarType(user.getAvatarType());
        dto.setAvatarInitialColor(user.getAvatarInitialColor());
        dto.setInitials(user.getInitials());
        dto.setCreatedAt(user.getCreatedAt());
        return dto;
    }
}
