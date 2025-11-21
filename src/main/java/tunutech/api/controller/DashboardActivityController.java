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
import tunutech.api.services.ActivityService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardActivityController {

    private final ActivityService activityService;

    /**
     * Récupérer les activités récentes pour le dashboard
     */
    @GetMapping("/recent-activities")
    public ResponseEntity<List<ActivityDTO>> getDashboardActivities() {
        log.info("Récupération des activités récentes pour le dashboard");
        // Retourne les 10 activités les plus récentes pour le dashboard
        List<ActivityDTO> activities = activityService.getRecentActivities(10);
        return ResponseEntity.ok(activities);
    }

    /**
     * Récupérer les activités non lues pour les notifications
     */
    @GetMapping("/notifications")
    public ResponseEntity<Map<String, Object>> getNotifications(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") int limit) {

        log.info("Récupération des notifications pour l'utilisateur: {}", userId);

        Long unreadCount = activityService.getUnreadCount(userId);
        List<ActivityDTO> recentUnread = activityService.getUserActivities(userId,
                PageRequest.of(0, limit, Sort.by("createdAt").descending())).getContent();

        Map<String, Object> response = Map.of(
                "unreadCount", unreadCount,
                "recentActivities", recentUnread
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Statistiques pour le dashboard admin
     */
    @GetMapping("/admin-stats")
    public ResponseEntity<Map<String, Object>> getAdminDashboardStats() {
        log.info("Récupération des statistiques pour le dashboard admin");

        Map<String, Object> stats = Map.of(
                "totalActivities", activityService.getTotalActivitiesCount(),
                "activitiesByType", activityService.getActivitiesCountByType(),
                "activitiesByCategory", activityService.getActivitiesCountByCategory(),
                "recentActivities", activityService.getRecentActivities(5)
        );

        return ResponseEntity.ok(stats);
    }
}