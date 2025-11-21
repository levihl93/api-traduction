package tunutech.api.services;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import tunutech.api.dtos.NotificationDTO;
import tunutech.api.dtos.NotificationRequest;
import tunutech.api.model.NotificationType;
import tunutech.api.model.RoleUser;
import tunutech.api.model.User;

import java.util.List;
import java.util.Map;

public interface NotificationService {
    // Notifications individuelles
    // 🔥 NOTIFICATIONS INDIVIDUELLES
    void notifyUser(Long userId, NotificationType type, String title, String message,
                    String actionUrl);

    // 🔥 NOTIFICATIONS DE GROUPE
    void notifyAllTranslators(NotificationType type, String title, String message,
                              Long projectId, String actionUrl);

    void notifyAllClients(NotificationType type, String title, String message,
                          Long projectId, String actionUrl);

    void notifyUserGroup(RoleUser roleUser, NotificationType type, String title,
                         String message, Long projectId, String actionUrl);

    // 📨 RÉCUPÉRATION DES NOTIFICATIONS
    List<NotificationDTO> getUserNotifications(Long userId);
    Page<NotificationDTO> getUserNotificationsPaginated(Long userId, Pageable pageable);
    List<NotificationDTO> getUnreadNotifications(Long userId);
    long getUnreadCount(Long userId);

    // ✅ GESTION DE LA LECTURE
    void markAsRead(Long userNotificationId, Long userId);
    void markAllAsRead(Long userId);

    // 🗑️ SUPPRESSION
    void deleteNotification(Long userNotificationId, Long userId);

    // 📊 STATISTIQUES
    Map<String, Long> getNotificationStats(Long userId);
}
