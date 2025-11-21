package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.Date;

@Table(name = "chatmessage")
@Entity
@Getter
@Setter
@ToString
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JoinColumn(name = "idchatroom",nullable = false)
    @ManyToOne
    private ChatRoom chatRoom;

    @JoinColumn(name = "iduser",nullable = false)
    @ManyToOne
    private  User user;

    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private Boolean isRead=false;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType type=MessageType.TEXT;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SenderRole senderRole;

    private LocalDateTime timestamp;
    public ChatMessage() {
        this.timestamp = LocalDateTime.now();
    }
    // CHAMPS AJOUTÉS POUR LES FICHIERS
    private String fileName;    // "document.pdf"
    private String fileUrl;     // "/uploads/abc123.pdf"
    private String fileType;    // "pdf", "image", "doc"
    private Long fileSize;      // 1048576 (en bytes)

    @CreationTimestamp
    @Column(updatable = false,name = "created_At")
    private Date created_At;
}
