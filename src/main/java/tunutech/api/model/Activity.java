package tunutech.api.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Table(name = "activity")
@Entity
@Getter
@Setter
@ToString
@Builder // ← AJOUTER CETTE LIGNE
@NoArgsConstructor // ← AJOUTER CETTE LIGNE
@AllArgsConstructor // ← AJOUTER CETTE LIGNE
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", unique = true, nullable = false)
    private String uuid;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ActivityType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 30)
    private ActivityGroup category;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "user_name", nullable = false, length = 255)
    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_role", nullable = false, length = 20)
    private RoleUser userRole;

    @Column(name = "user_email", length = 255)
    private String userEmail;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "project_id")
    private Long projectId;

    @Column(name = "project_name", length = 255)
    private String projectName;

    @Column(name = "beneficiaire", length = 255)
    private String beneficiaire;

    // CORRECTION : Utilisation de @JdbcTypeCode pour JSON
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "json")
    private Map<String, Object> metadata = new HashMap<>();

    @Column(name = "is_read")
    private Boolean isRead = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", length = 20)
    private PriorityType priority = PriorityType.MEDIUM; // Correction : supprimer le .MEDIUM en double

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}