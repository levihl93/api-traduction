package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Table(name = "traducteurlangue")
@Entity
@Getter
@Setter
@ToString
public class TraducteurLangue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @JoinColumn(name = "idtraducteur")
    @ManyToOne
    private Traducteur traducteur;

    @JoinColumn(name = "idlangue")
    @ManyToOne
    private Langue langue;

    @CreationTimestamp
    @Column(updatable = false,name = "created_At")
    private Date created_At;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private Date updated_At;
}