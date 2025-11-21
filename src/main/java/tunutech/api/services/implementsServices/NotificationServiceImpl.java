package tunutech.api.services.implementsServices;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tunutech.api.dtos.NotificationDTO;
import tunutech.api.dtos.NotificationRequest;
import tunutech.api.model.*;
import tunutech.api.repositories.NotificationRepository;
import tunutech.api.repositories.NotificationUserRepository;
import tunutech.api.repositories.UserRepository;
import tunutech.api.services.NotificationService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationUserRepository userNotificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
                                   NotificationUserRepository userNotificationRepository,
                                   UserRepository userRepository,
                                   SimpMessagingTemplate messagingTemplate) {
        this.notificationRepository = notificationRepository;
        this.userNotificationRepository = userNotificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }
    // ==================== NOTIFICATIONS INDIVIDUELLES ====================

    @Override
    public void notifyUser(Long userId, NotificationType type, String title, String message,
                            String actionUrl) {

        User user = findUserById(userId);
        Notification notification = createNotification(type, title, message, actionUrl);
        NotificationUser userNotification = createUserNotification(user, notification);

        sendRealTimeNotification(user, notification);
        //log.info("✅ Notification envoyée à l'utilisateur {}: {}", userId, title);
    }

    @Override
    public void notifyAllTranslators(NotificationType type, String title, String message, Long projectId, String actionUrl) {
        List<User> translators = userRepository.findByRoleUserAndActive(RoleUser.TRANSLATOR, true);

        if (translators.isEmpty()) {
            throw  new RuntimeException("⚠️ Aucun traducteur actif trouvé");
        }

        notifyUserList(translators, type, title, message, actionUrl);
    }

    @Override
    public void notifyAllClients(NotificationType type, String title, String message, Long projectId, String actionUrl) {
        List<User> clients = userRepository.findByRoleUserAndActive(RoleUser.CLIENT, true);

        if (clients.isEmpty()) {
            //log.warn("⚠️ Aucun client actif trouvé");
            return;
        }

        //notifyUserList(clients, type, title, message, actionUrl);
        //log.info("📤 Notification envoyée à {} clients: {}", clients.size(), title);
    }

    @Override
    public void notifyUserGroup(RoleUser roleUser, NotificationType type, String title, String message, Long projectId, String actionUrl) {
        List<Long> userIds=new ArrayList<>();
        List<User>users=userRepository.findByRoleUser(roleUser);
        for(User user:users)
        {
            userIds.add(user.getId());
        }
        if (userIds.isEmpty()) {
           throw new RuntimeException("List Group Empty");
        }
        Notification notification = createNotification(type, title, message, actionUrl);
        for(Long ids:userIds)
        {
            this.notifyUser(ids,type,title,message,actionUrl);
        }
//        List<User> users = userRepository.findAllById(userIds);
        //notifyUserList(users, type, title, message, actionUrl);
       // log.info("📤 Notification envoyée à {} utilisateurs: {}", users.size(), title);
    }

    // ==================== NOTIFICATIONS DE GROUPE ====================


    // ==================== RÉCUPÉRATION DES NOTIFICATIONS ====================

    @Override
    public List<NotificationDTO> getUserNotifications(Long userId) {
        return userNotificationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertUserNotificationToDTO) // CORRIGÉ
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDTO> getUserNotificationsPaginated(Long userId, Pageable pageable) {
        return userNotificationRepository.findByUserId(userId, pageable)
                .map(this::convertUserNotificationToDTO); // CORRIGÉ
    }

    @Override
    public List<NotificationDTO> getUnreadNotifications(Long userId) {
        return userNotificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::convertUserNotificationToDTO) // CORRIGÉ
                .collect(Collectors.toList());
    }

    @Override
    public long getUnreadCount(Long userId) {
        return userNotificationRepository.countByUserIdAndIsReadFalse(userId);
    }

    // ==================== GESTION DE LA LECTURE ====================

    @Transactional
    @Override
    public void markAsRead(Long userNotificationId, Long userId) {
        int updated = userNotificationRepository.markAsRead(userNotificationId, userId, LocalDateTime.now());

        if (updated == 0) {
            throw new RuntimeException("Notification non trouvée ou accès non autorisé");
        }

        long newCount = getUnreadCount(userId);
        sendUnreadCountUpdate(userId, newCount);
       // log.debug("📖 Notification {} marquée comme lue par l'utilisateur {}", userNotificationId, userId);
    }

    @Transactional

    @Override
    public void markAllAsRead(Long userId) {
        int updated = userNotificationRepository.markAllAsRead(userId, LocalDateTime.now());

        if (updated > 0) {
            sendUnreadCountUpdate(userId, 0L);
            //log.debug("📖 Toutes les notifications marquées comme lues par l'utilisateur {}", userId);
        }
    }

    // ==================== SUPPRESSION ====================

    @Override
    public void deleteNotification(Long userNotificationId, Long userId) {
       NotificationUser userNotification = userNotificationRepository.findById(userNotificationId)
                .orElseThrow(() -> new RuntimeException("Notification non trouvée"));

        if (!userNotification.getUser().getId().equals(userId)) {
            throw new RuntimeException("Accès non autorisé à cette notification");
        }

        userNotificationRepository.delete(userNotification);

        if (!userNotification.getIsRead()) {
            long newCount = getUnreadCount(userId);
            sendUnreadCountUpdate(userId, newCount);
        }

       // log.debug("🗑️ Notification {} supprimée par l'utilisateur {}", userNotificationId, userId);
    }

    // ==================== STATISTIQUES ====================

    @Override
    public Map<String, Long> getNotificationStats(Long userId) {
        long total = userNotificationRepository.countByUserId(userId);
        long unread = userNotificationRepository.countByUserIdAndIsReadFalse(userId);
        long read = total - unread;

        return Map.of(
                "total", total,
                "unread", unread,
                "read", read
        );
    }

    // ==================== MÉTHODES PRIVÉES ====================

    private void notifyUserList(List<User> users, NotificationType type, String title,
                                String message, String actionUrl) {

        Notification notification = createNotification(type, title, message, actionUrl);
        List<NotificationUser> userNotifications = new ArrayList<>();

        for (User user : users) {
            NotificationUser userNotification = createUserNotification(user, notification);
            userNotifications.add(userNotification);
            sendRealTimeNotification(user, notification);
        }

        userNotificationRepository.saveAll(userNotifications);
    }

    private User findUserById(Long userId) {
        return userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'ID: " + userId));
    }

    private Notification createNotification(NotificationType type, String title, String message, String actionUrl) {
        Notification notification = new Notification();
        notification.setNotificationType(type);
        notification.setTitle(title);
        notification.setMessage(message);
        notification.setActionUrl(actionUrl);
        return notificationRepository.save(notification);
    }

    private NotificationUser createUserNotification(User user, Notification notification) {
        NotificationUser userNotification = new NotificationUser();
        userNotification.setUser(user);
        userNotification.setNotification(notification);
        return userNotificationRepository.save(userNotification);
    }

    private void sendRealTimeNotification(User user, Notification notification) {
        try {
            NotificationDTO dto = convertNotificationToDTO(notification);
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/notifications",
                    dto
            );
        } catch (Exception e) {
            //log.error("❌ Erreur WebSocket pour {}: {}", user.getEmail(), e.getMessage());
        }
    }

    private void sendUnreadCountUpdate(Long userId, long count) {
        try {
            User user = userRepository.findById(Math.toIntExact(userId)).orElse(null);
            if (user != null) {
                messagingTemplate.convertAndSendToUser(
                        user.getEmail(),
                        "/queue/notifications/count",
                        Map.of("unreadCount", count)
                );
            }
        } catch (Exception e) {
            //log.error("❌ Erreur envoi compteur pour {}: {}", userId, e.getMessage());
        }
    }

    // ==================== MÉTHODES DE CONVERSION ====================

    // Renommer pour éviter l'ambigüité
    private NotificationDTO convertUserNotificationToDTO(NotificationUser userNotification) {
        Notification notification = userNotification.getNotification();

        return NotificationDTO.builder()
                .id(userNotification.getId())
                .notificationType(notification.getNotificationType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .actionUrl(notification.getActionUrl())
                .read(userNotification.getIsRead())
                .readAt(userNotification.getReadAt())
                .createdAt(userNotification.getCreatedAt())
                .build();
    }

    // Renommer pour éviter l'ambigüité
    private NotificationDTO convertNotificationToDTO(Notification notification) {
        return NotificationDTO.builder()
                .id(notification.getId())
                .notificationType(notification.getNotificationType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .actionUrl(notification.getActionUrl())
                .read(false)
                .build();
    }
}
