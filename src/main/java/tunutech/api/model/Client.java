package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Table(name = "client")
@Entity
@Getter
@Setter
@ToString
public class Client{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = true)
    private String firstname;

    @Column(nullable = true)
    private String lastname;

    @Column(nullable = true)
    private String denomination;

    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String telephone;

    @Column(nullable = true)
    private String sexe;

    @Column(nullable = false)
    private String pays;
    @Column(nullable = false)
    private String adresse;
    @Column(nullable = false)
    private String secteur;
    @Column(nullable = false)
    private Boolean present=true;

    @Column(nullable = false)
    private Boolean active=true;

    @CreationTimestamp
    @Column(updatable = false,name = "created_At")
    private Date created_At;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private Date update_At;

    public String getFullName()
    {
        String identite="";
        if(this.getDenomination()==null)
        {
            identite=this.getFirstname()+" "+this.getLastname();
        }else {
            identite=this.getDenomination();
        }
        return  identite;
    }
}
