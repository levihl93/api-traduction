package tunutech.api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tunutech.api.dtos.ActivityDTO;
import tunutech.api.model.ActivityType;
import tunutech.api.model.User;

import java.util.List;
import java.util.Map;

public interface ActivityService {
    // Méthodes de logging d'activités
    void logUserActivity(User user, ActivityType type, String description);
    void logProjectActivity(User user, ActivityType type, String projectName, String beneficiaire,Long projectId, String description);
    void logPaymentActivity(User user, ActivityType type, String projectName, Long projectId, Map<String, Object> metadata);
    void logSystemActivity(ActivityType type, String description, Map<String, Object> metadata);

    // Méthodes de récupération d'activités
    Page<ActivityDTO> getRecentActivities(Pageable pageable);
    List<ActivityDTO> getUnreadActivities();
    Page<ActivityDTO> getUserActivities(Long userId, Pageable pageable);
    Page<ActivityDTO> getProjectActivities(Long projectId, Pageable pageable);
    List<ActivityDTO> getActivitiesByType(ActivityType type);
    List<ActivityDTO> getRecentActivities(int limit);
    List<ActivityDTO> getRecentActivitiesOfProject(int limit,Long idproject);
    ActivityDTO getActivityById(Long activityId); // AJOUTÉE

    // Méthodes de gestion du statut de lecture
    void markAsRead(Long activityId);
    void markAllAsRead(Long userId);
    Long getUnreadCount(Long userId);

    // Méthodes statistiques
    Long getTotalActivitiesCount();
    Map<String, Long> getActivitiesCountByType();
    Map<String, Long> getActivitiesCountByCategory();

    // Méthodes de suppression
    void deleteActivity(Long activityId);
    void deleteOldActivities(int days);
}
