package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.beans.factory.annotation.Autowired;
import tunutech.api.repositories.ProjetLangueSourceRepository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Table(name = "project")
@Entity
@Setter
@Getter
@ToString
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
   @Column(nullable = false,unique = true)
    private String code;
   @JoinColumn(name = "idclient",nullable = false)
   @ManyToOne
    private Client client;


    @Column(nullable = false)
    private Boolean valider=false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectStatus projectStatus;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument typeDocument;

    @Column(nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriorityType priorityType;

    @Column(nullable = false)
    private Float wordscount;
    @Column(nullable = false)
    private Float priceperWord;

    @Column(nullable = false)
    private Float estimatedPrice;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Boolean annuler=false;

    @Column(nullable = false)
    private Boolean terminer=false;

    @Column(nullable = false)
    private Date datevoulue;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;
}
