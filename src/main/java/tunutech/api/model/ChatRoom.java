package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Table(name = "chatroom")
@Entity
@Getter
@Setter
@ToString
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false,unique = true)
    private String roomId;

    @JoinColumn(name = "idclient",nullable = true)
    @ManyToOne(optional = true)
    private Client client;

    @JoinColumn(name = "idtranslator",nullable = true)
    @ManyToOne(optional = true)
    private Traducteur traducteur;

    @JoinColumn(name = "idtranslator2",nullable = true)
    @ManyToOne(optional = true)
    private Traducteur traducteur2;

    @JoinColumn(name = "idproject",nullable = false)
    @ManyToOne()
    private  Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChatStatus chatStatus;

    @CreationTimestamp
    @Column(updatable = false,name = "created_At")
    private Date created_At;
}
