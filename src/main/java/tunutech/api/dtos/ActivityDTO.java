package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import tunutech.api.model.ActivityGroup;
import tunutech.api.model.ActivityType;
import tunutech.api.model.PriorityType;
import tunutech.api.model.RoleUser;

import java.time.LocalDateTime;
import java.util.Map;

@Getter
@Setter
public class ActivityDTO {
    private Long id;
    private String uuid;
    private ActivityType type;
    private ActivityGroup category;
    private String title;
    private String description;

    // Informations utilisateur
    private Long userId;
    private String userName;
    private String beneficiaire;
    private String userEmail;
    private RoleUser userRole;

    // Informations projet (optionnelles)
    private Long projectId;
    private Long contractId;
    private String projectName;

    // Métadonnées
    private Map<String, Object> metadata;
    private Boolean isRead;
    private PriorityType priority;

    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Méthodes utilitaires
    public String getTimeAgo() {
        // Implémentation pour afficher "il y a 2 heures", etc.
        return formatTimeAgo(createdAt);
    }

    public String getIcon() {
        return getIconForActivityType(type);
    }

    public String getColor() {
        return getColorForPriority(priority);
    }

    private String formatTimeAgo(LocalDateTime date) {
        // Implémentation de la logique de formatage
        return "il y a 2 heures";
    }

    private String getIconForActivityType(ActivityType type) {
        switch (type) {
            case USER_REGISTERED: return "👤";
            case PROJECT_CREATED: return "📁";
            case TRANSLATION_COMPLETED: return "✅";
            case PAYMENT_PROCESSED: return "💰";
            default: return "📢";
        }
    }

    private String getColorForPriority(PriorityType priority) {
        switch (priority) {
            case LOW: return "gray";
            case MEDIUM: return "blue";
            case HIGH: return "orange";
            case URGENT: return "red";
            default: return "blue";
        }
    }
}
