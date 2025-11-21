package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Table(name = "langue")
@Entity
@Getter
@Setter
@ToString
public class Langue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @Column(nullable = false,unique = true)
    private String name;

    @Column(nullable = false,unique = true)
    private String code;

    @Column()
    private boolean present=true;

    @Column(nullable = false)
    private boolean active=true;

    @CreationTimestamp
    @Column(updatable = false,name = "created_At")
    private Date created_At;

    @UpdateTimestamp
    @Column(name = "updated_At")
    private Date updated_At;
}
