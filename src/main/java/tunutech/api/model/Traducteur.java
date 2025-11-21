package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Table(name = "traducteur")
@Getter
@Setter
@ToString
@Entity
public class Traducteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

@Column(nullable = false)
    private String firstname;

@Column(nullable = false)
    private String lastname;

@Column(nullable = false,unique = true)
    private String email;

@Column(nullable = false)
    private String telephone;

@Column(nullable = false)
    private String sexe;

@Column(nullable = false)
    private String pays;
    @Column(nullable = false)
    private String adresse;
@Column(nullable = false)
    private boolean present=true;

@Column(nullable = false)
    private boolean active=false;

    @Column(nullable = true)
    private boolean available=false;

    public String getFullName()
    {
        String identite="";
        identite=this.getFirstname()+" "+this.getLastname();
        return  identite;
    }
@CreationTimestamp
@Column(updatable = false,name = "created_At")
    private Date created_At;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private Date update_At;
}