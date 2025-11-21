package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Table(name = "complexity")
@Entity
@Getter
@Setter
@ToString
public class Complexity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double multiplicationCompexity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProjectComplexity projectComplexity;

}
