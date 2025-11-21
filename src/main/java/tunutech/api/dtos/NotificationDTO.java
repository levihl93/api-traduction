package tunutech.api.dtos;

import lombok.*;
import tunutech.api.model.NotificationType;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationType notificationType;
    private String title;
    private String message;
    private Long projectId;
    private String projectName;
    private String translatorName;
    private String actionUrl;
    private boolean read;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;

    // Champ calculé pour l'UI
    /*public String getTimeAgo() {
        // Implémentation pour "Il y a 2 minutes"
        return formatTimeAgo(createdAt);
    }*/
}
