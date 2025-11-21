package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "contrat")
@Entity
@Data
public class Contrat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "projetId")
    private Project project;

    @Column(nullable = false)
    private Float nombreMots;

    @Column(nullable = false,unique = true)
    private String code;

    @Column(nullable = false)
    private String clientName;

    @Column(nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    private String clientAdresse;
    @Column(nullable = false)
    private String clientPays;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private  ProjectComplexity projectComplexity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Devise devise;

    @Column(nullable = false)
    private Double montantContrat;

    @Column(nullable = false)
    private Double majorationPourcentage;

    @Column(nullable = false)
    private Double montatMajoration;

    @Column(nullable = false)
    private Date echeanceContrat;

    @Column(nullable = false)
    private Integer nombreJours;

    @Column(nullable = false, length = 2000)
    private String conditionsSpeciales;

    @Column(nullable = false, length = 2000)
    private String conditionsGenerales;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContratStatut contratStatut;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "approved_At",nullable = true)
    private LocalDateTime approved_At;

    @CreationTimestamp
    @Column(updatable = false,name = "created_At")
    private Date created_At;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private Date update_At;
}
