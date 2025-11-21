package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "notificationsuser")
@Entity
@Getter
@Setter
@ToString
public class NotificationUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "userid",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @JoinColumn(name = "notificationid",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Notification notification;

    @Column(nullable = false)
    private Boolean isRead=false;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private LocalDateTime createdAt;


    @Column(name = "readAt")
    private LocalDateTime readAt;

}
