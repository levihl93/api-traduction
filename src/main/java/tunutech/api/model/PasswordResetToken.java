package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
public class PasswordResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @Column(name = "expires_at", nullable = false)
    private Date expiresAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public static PasswordResetToken generate(User user, int expirationMinutes) {
        PasswordResetToken prt = new PasswordResetToken();
        prt.setToken(UUID.randomUUID().toString());
        prt.setUser(user);

        Date now = new Date();
        prt.setExpiresAt(new Date(now.getTime() + expirationMinutes * 60 * 1000));

        return prt;
    }
}
