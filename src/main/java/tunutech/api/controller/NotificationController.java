package tunutech.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tunutech.api.dtos.*;
import tunutech.api.model.User;
import tunutech.api.services.NotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final NotificationService notificationService;

    // 🔥 CREER UNE NOTIFICATION
    @PostMapping
    public ResponseEntity<ApiResponseDTO<Void>> createNotification(
            @Valid @RequestBody NotificationRequestDTO request) {

        notificationService.notifyUser(
                request.getUserId(),
                request.getNotificationType(),
                request.getTitle(),
                request.getMessage(),
                request.getActionUrl()
        );

        return ResponseEntity.ok(ApiResponseDTO.success(
                null,
                "Notification envoyée avec succès"
        ));
    }

    @PostMapping("sendforGroup")
    public ResponseEntity<?>createNotifforGroup(@RequestBody NotificationGroupRequest notificationGroupRequest)
    {
        notificationService.notifyUserGroup(
                notificationGroupRequest.getRoleUser(),
                notificationGroupRequest.getNotificationType(),
                notificationGroupRequest.getTitle(),
                notificationGroupRequest.getMessage(),
                notificationGroupRequest.getProjectId(),
                notificationGroupRequest.getActionUrl()
        );
        return ResponseEntity.ok(ApiResponseDTO.success(
                null,
                "Notification envoyée avec succès"
        ));
    }

    // 📨 RECUPERER LES NOTIFICATIONS
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<NotificationDTO>>> getUserNotifications(
            @AuthenticationPrincipal User user) {
                    System.out.println(user.getEmail());
        List<NotificationDTO> notifications = notificationService.getUserNotifications(user.getId());
        return ResponseEntity.ok(ApiResponseDTO.success(notifications));
    }

    // 📨 RECUPERER AVEC PAGINATION
    @GetMapping("/paginated")
    public ResponseEntity<ApiResponseDTO<Page<NotificationDTO>>> getUserNotificationsPaginated(
            @AuthenticationPrincipal User user,
            Pageable pageable) {

        Page<NotificationDTO> notifications = notificationService.getUserNotificationsPaginated(user.getId(), pageable);
        return ResponseEntity.ok(ApiResponseDTO.success(notifications));
    }

    // 🔢 COMPTER LES NON LUES
    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponseDTO<Long>> getUnreadCount(@AuthenticationPrincipal User user) {
        long count = notificationService.getUnreadCount(user.getId());
        return ResponseEntity.ok(ApiResponseDTO.success(count));
    }
    // 📋 RECUPERER LES NON LUES
    @GetMapping("/unread")
    public ResponseEntity<ApiResponseDTO<List<NotificationDTO>>> getUnreadNotifications(
            @AuthenticationPrincipal User user) {
        List<NotificationDTO> notifications = notificationService.getUnreadNotifications(user.getId());
        return ResponseEntity.ok(ApiResponseDTO.success(notifications));
    }

    // ✅ MARQUER COMME LUE
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponseDTO<Void>> markAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user) {
    System.out.println("notif");
        notificationService.markAsRead(notificationId, user.getId());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Notification marquée comme lue"));
    }

    // ✅ TOUT MARQUER COMME LU
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponseDTO<Void>> markAllAsRead(@AuthenticationPrincipal User user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Toutes les notifications marquées comme lues"));
    }

    // 🗑️ SUPPRIMER
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal User user) {

        notificationService.deleteNotification(notificationId, user.getId());
        return ResponseEntity.ok(ApiResponseDTO.success(null, "Notification supprimée"));
    }

    // 📊 STATISTIQUES
    @GetMapping("/stats")
    public ResponseEntity<ApiResponseDTO<NotificationStatsDTO>> getNotificationStats(
            @AuthenticationPrincipal User user) {

        var stats = notificationService.getNotificationStats(user.getId());
        NotificationStatsDTO statsDTO = new NotificationStatsDTO(
                stats.get("total"),
                stats.get("unread"),
                stats.get("read")
        );
        return ResponseEntity.ok(ApiResponseDTO.success(statsDTO));
    }
}
