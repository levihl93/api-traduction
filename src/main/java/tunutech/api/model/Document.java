package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@Table(name = "document")
@Entity
@Getter
@Setter
@ToString
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String originalName;        // Nom original du fichier

    @Column(nullable = false)
    private String storedName;          // Nom unique généré sur le serveur

    @Column(nullable = false)
    private String filePath;            // Chemin complet sur le serveur

    @Column(nullable = false)
    private Long fileSize;              // Taille en bytes

    private String contentType;         // Type MIME

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument typeDocument;

    @Column(nullable = false)
    private Float wordsCount;
    private String targetLanguage;      // Langue cible

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)

    private DocumentStatus status;

    @JoinColumn(name = "idprojet")
    @ManyToOne
    private Project project;

    @Column(nullable = false)
    private LocalDateTime uploadDate;

    private LocalDateTime translationDate;

    private LocalDateTime completedDate;

    private String checksum;            // MD5 pour vérification intégrité

    private String uploadedBy;          // Utilisateur qui a uploadé

    private String translator;          // Traducteur assigné

    private String notes;               // Notes supplémentaires

    // Constructeurs
    public Document() {
        this.uploadDate = LocalDateTime.now();
        this.status = DocumentStatus.UPLOADED;
    }

    public Document(String originalName,Long idproject, String sourceLanguage,Float wordscount) {
        this();
        this.originalName = originalName;
        this.wordsCount=wordscount;
        this.getProject().setId(idproject);
    }

}
