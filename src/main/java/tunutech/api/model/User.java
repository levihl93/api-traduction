package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Table(name = "users")
@Entity
@Getter
@Setter
@ToString
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(unique = true, length = 100, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // Champs pour l'avatar
    @Column(name = "avatar_url")
    private String avatarUrl;

    @Column(name = "avatar_type")
    @Enumerated(EnumType.STRING)
    private AvatarType avatarType = AvatarType.INITIALS;

    @Column(name = "avatar_initial_color")
    private String avatarInitialColor = "#667eea";

    @JoinColumn(name = "idtraducteur",nullable = true)
   @ManyToOne(optional = true)
    private Traducteur traducteur;

    @JoinColumn(name = "idclient",nullable = true)
    @ManyToOne(optional = true)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoleUser roleUser;

    @Column(nullable = false)
    private boolean present=true;

    @Column(nullable = false)
    private boolean active=false;

    public String getFullName() {
        if (this.traducteur != null) {
            return this.traducteur.getFullName();
        } else if (this.client != null) {
            return this.client.getFullName();
        }
        return this.email; // Fallback si ni traducteur ni client
    }

    // Méthode utilitaire pour les initiales
    public String getInitials() {
        if (this.getFullName() == null || this.getFullName().trim().isEmpty()) {
            return "?";
        }
        String[] names = this.getFullName().split(" ");
        if (names.length == 1) {
            return names[0].substring(0, Math.min(2, names[0].length())).toUpperCase();
        }
        return (names[0].charAt(0) + "" + names[names.length - 1].charAt(0)).toUpperCase();
    }

    // Méthode pour obtenir l'URL de l'avatar (logique métier)
    public String getAvatarDisplayUrl() throws NoSuchAlgorithmException {
        return switch (avatarType) {
            case UPLOAD -> avatarUrl;
            case INITIALS -> null; // Pas d'URL, on utilise les initiales côté frontend
            case GRAVATAR -> generateGravatarUrl();
            default -> null;
        };
    }

    private String generateGravatarUrl() throws NoSuchAlgorithmException {
        // Implémentation Gravatar si besoin
        return "https://www.gravatar.com/avatar/" +
                java.security.MessageDigest.getInstance("MD5")
                        .digest(email.toLowerCase().getBytes())
                        .toString() + "?d=identicon";
    }

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + roleUser.name()));
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
