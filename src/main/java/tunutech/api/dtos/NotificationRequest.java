package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.NotificationType;

@Getter
@Setter
public class NotificationRequest {
    private NotificationType notificationType;
    private String title;
    private String message;
    private Long projectId;
    private String projectName;
    private String translatorName;
    private String actionUrl;
}
