package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.NotificationType;
import tunutech.api.model.RoleUser;

@Getter
@Setter
public class NotificationGroupRequest {
    private NotificationType notificationType;
    private RoleUser roleUser;
    private String title;
    private String message;
    private Long projectId;
    private String projectName;
    private String translatorName;
    private String actionUrl;
}
