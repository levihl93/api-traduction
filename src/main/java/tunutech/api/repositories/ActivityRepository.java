package tunutech.api.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Range;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tunutech.api.model.Activity;
import tunutech.api.model.ActivityGroup;
import tunutech.api.model.ActivityType;

import java.time.LocalDateTime;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    // Méthodes de recherche basiques
    Page<Activity> findAllByOrderByCreatedAtDesc(Pageable pageable);
    Page<Activity> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Page<Activity> findByProjectIdOrderByCreatedAtDesc(Long projectId, Pageable pageable);
    List<Activity> findByIsReadFalseOrderByCreatedAtDesc();
    List<Activity> findByTypeOrderByCreatedAtDesc(ActivityType type);
    List<Activity> findByUserIdAndIsReadFalse(Long userId);

    // Méthodes de comptage
    Long countByUserIdAndIsReadFalse(Long userId);

    // Requêtes personnalisées
    @Query("SELECT a FROM Activity a ORDER BY a.createdAt DESC LIMIT :limit")
    List<Activity> findRecentActivities(@Param("limit") int limit);

    @Query(value = "SELECT a.* FROM activity a WHERE a.project_id = :idProject ORDER BY a.created_at DESC LIMIT :limit",
            nativeQuery = true)
    List<Activity> findRecentActivitiesofProject(@Param("idProject") Long idProject, @Param("limit") int limit);

    @Query("SELECT a.type, COUNT(a) FROM Activity a GROUP BY a.type")
    List<Object[]> countActivitiesByType();

    @Query("SELECT a.category, COUNT(a) FROM Activity a GROUP BY a.category")
    List<Object[]> countActivitiesByCategory();

    @Modifying
    @Query("DELETE FROM Activity a WHERE a.createdAt < :cutoffDate")
    int deleteByCreatedAtBefore(@Param("cutoffDate") LocalDateTime cutoffDate);

    // Statistiques avancées
    @Query("SELECT COUNT(a) FROM Activity a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    Long countActivitiesBetweenDates(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT FUNCTION('DATE', a.createdAt), COUNT(a) FROM Activity a " +
            "WHERE a.createdAt BETWEEN :startDate AND :endDate " +
            "GROUP BY FUNCTION('DATE', a.createdAt) " +
            "ORDER BY FUNCTION('DATE', a.createdAt)")
    List<Object[]> getDailyActivityCount(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
}
