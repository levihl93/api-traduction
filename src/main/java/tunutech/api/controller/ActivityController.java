package tunutech.api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.ActivityDTO;
import tunutech.api.model.ActivityType;
import tunutech.api.services.ActivityService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activities")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {

    private final ActivityService activityService;

    /**
     * Récupérer une activité par son ID
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable Long activityId) {
        log.info("Récupération de l'activité: {}", activityId);
        ActivityDTO activity = activityService.getActivityById(activityId);
        return ResponseEntity.ok(activity);
    }

    /**
     * Marquer une activité comme lue
     */
    @PutMapping("/{activityId}/read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long activityId) {
        log.info("Marquage de l'activité {} comme lue", activityId);
        activityService.markAsRead(activityId);
        return ResponseEntity.ok().build();
    }

    /**
     * Supprimer une activité
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> deleteActivity(@PathVariable Long activityId) {
        log.info("Suppression de l'activité: {}", activityId);
        activityService.deleteActivity(activityId);
        return ResponseEntity.ok().build();
    }


    /**
     * Récupérer toutes les activités avec pagination
     */
    @GetMapping
    public ResponseEntity<Page<ActivityDTO>> getAllActivities(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("Récupération des activités - page: {}, size: {}, sort: {}", page, size, sortBy);

        Sort sort = direction.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ActivityDTO> activities = activityService.getRecentActivities(pageable);

        return ResponseEntity.ok(activities);
    }

    /**
     * Récupérer les activités d'un utilisateur spécifique
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ActivityDTO>> getUserActivities(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Récupération des activités pour l'utilisateur: {}", userId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityDTO> activities = activityService.getUserActivities(userId, pageable);

        return ResponseEntity.ok(activities);
    }

    /**
     * Récupérer les activités d'un projet spécifique
     */
    @GetMapping("/project/{projectId}")
    public ResponseEntity<Page<ActivityDTO>> getProjectActivities(
            @PathVariable Long projectId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        log.info("Récupération des activités pour le projet: {}", projectId);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<ActivityDTO> activities = activityService.getProjectActivities(projectId, pageable);

        return ResponseEntity.ok(activities);
    }

    /**
     * Récupérer les activités non lues
     */
    @GetMapping("/unread")
    public ResponseEntity<List<ActivityDTO>> getUnreadActivities() {
        log.info("Récupération des activités non lues");
        List<ActivityDTO> unreadActivities = activityService.getUnreadActivities();
        return ResponseEntity.ok(unreadActivities);
    }

    /**
     * Récupérer le nombre d'activités non lues pour un utilisateur
     */
    @GetMapping("/unread/count/{userId}")
    public ResponseEntity<Map<String, Long>> getUnreadCount(@PathVariable Long userId) {
        log.info("Récupération du nombre d'activités non lues pour l'utilisateur: {}", userId);
        Long count = activityService.getUnreadCount(userId);
        return ResponseEntity.ok(Map.of("unreadCount", count));
    }

    /**
     * Récupérer les activités par type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<ActivityDTO>> getActivitiesByType(@PathVariable ActivityType type) {
        log.info("Récupération des activités par type: {}", type);
        List<ActivityDTO> activities = activityService.getActivitiesByType(type);
        return ResponseEntity.ok(activities);
    }

    /**
     * Récupérer les activités récentes (limitées)
     */
    @GetMapping("/recent")
    public ResponseEntity<List<ActivityDTO>> getRecentActivities(
            @RequestParam(defaultValue = "10") int limit) {

        log.info("Récupération des {} activités récentes", limit);
        List<ActivityDTO> recentActivities = activityService.getRecentActivities(limit);
        return ResponseEntity.ok(recentActivities);
    }


    /**
     * Marquer toutes les activités d'un utilisateur comme lues
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<Void> markAllAsRead(@PathVariable Long userId) {
        log.info("Marquage de toutes les activités comme lues pour l'utilisateur: {}", userId);
        activityService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    /**
     * Récupérer les statistiques des activités
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getActivityStats() {
        log.info("Récupération des statistiques des activités");

        Map<String, Object> stats = Map.of(
                "total", activityService.getTotalActivitiesCount(),
                "byType", activityService.getActivitiesCountByType(),
                "byCategory", activityService.getActivitiesCountByCategory()
        );

        return ResponseEntity.ok(stats);
    }
    /**
     * Supprimer les anciennes activités
     */
    @DeleteMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupOldActivities(
            @RequestParam(defaultValue = "30") int days) {

        log.info("Nettoyage des activités plus anciennes que {} jours", days);
        activityService.deleteOldActivities(days);

        Map<String, Object> response = Map.of(
                "message", "Activités plus anciennes que " + days + " jours supprimées",
                "days", days
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Endpoint de santé du contrôleur
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "Activity Controller is healthy"));
    }
}