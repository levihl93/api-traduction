package tunutech.api.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tunutech.api.model.NotificationType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequestDTO {
    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;

    @NotNull(message = "Le type de notification est obligatoire")
    private NotificationType notificationType;

    @NotBlank(message = "Le titre est obligatoire")
    private String title;

    @NotBlank(message = "Le message est obligatoire")
    private String message;

    private Long projectId;
    private String actionUrl;
}
