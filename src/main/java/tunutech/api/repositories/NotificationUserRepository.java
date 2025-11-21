package tunutech.api.repositories;

import io.micrometer.common.KeyValues;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tunutech.api.model.NotificationUser;

import java.time.LocalDateTime;
import java.util.List;

public interface NotificationUserRepository extends JpaRepository<NotificationUser, Long> {
    // Notifications d'un utilisateur spécifique (paginated)
    @Query("SELECT un FROM NotificationUser un WHERE un.user.id = :userId ORDER BY un.createdAt DESC")
    Page<NotificationUser> findByUserId(@Param("userId") Long userId, Pageable pageable);

    // Notifications d'un utilisateur spécifique (liste simple)
    @Query("SELECT un FROM NotificationUser un WHERE un.user.id = :userId ORDER BY un.createdAt DESC")
    List<NotificationUser> findByUserId(@Param("userId") Long userId);

    // Notifications non lues
    @Query("SELECT un FROM NotificationUser un WHERE un.user.id = :userId AND un.isRead = false ORDER BY un.createdAt DESC")
    List<NotificationUser> findUnreadByUserId(@Param("userId") Long userId);

    // Compter les non lues
    long countByUserIdAndIsReadFalse(Long userId);

    // Marquer comme lu
    @Modifying
    @Query("UPDATE NotificationUser un SET un.isRead = true, un.readAt = :now WHERE un.id = :id AND un.user.id = :userId")
    int markAsRead(@Param("id") Long id, @Param("userId") Long userId, @Param("now") LocalDateTime now);

    // Marquer toutes comme lues
    @Modifying
    @Query("UPDATE NotificationUser un SET un.isRead = true, un.readAt = :now WHERE un.user.id = :userId AND un.isRead = false")
    int markAllAsRead(@Param("userId") Long userId, @Param("now") LocalDateTime now);

    long countByUserId(Long userId);

    // 📨 TROUVER TOUTES LES NOTIFICATIONS D'UN UTILISATEUR (plus récentes en premier)
    @Query("SELECT un FROM NotificationUser un WHERE un.user.id = :userId ORDER BY un.createdAt DESC")
    List<NotificationUser> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 📨 TROUVER LES NOTIFICATIONS NON LUES (MÉTHODE AJOUTÉE)
    @Query("SELECT un FROM NotificationUser un WHERE un.user.id = :userId AND un.isRead = false ORDER BY un.createdAt DESC")
    List<NotificationUser> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(@Param("userId") Long userId);

    // 📨 TROUVER LES NOTIFICATIONS LUES
    @Query("SELECT un FROM NotificationUser un WHERE un.user.id = :userId AND un.isRead = true ORDER BY un.createdAt DESC")
    List<NotificationUser> findByUserIdAndIsReadTrueOrderByCreatedAtDesc(@Param("userId") Long userId);

}
