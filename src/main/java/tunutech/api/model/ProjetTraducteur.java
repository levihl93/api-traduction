package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table(name = "projettraducteur")
@Entity
@Getter
@Setter
@ToString
public class ProjetTraducteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "idproject")
    @ManyToOne
    private Project project;

    @JoinColumn(name = "idtraducteur")
    @ManyToOne
    private Traducteur traducteur;

    @JoinColumn(name = "iduser")
    @ManyToOne
    private User user;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

}
