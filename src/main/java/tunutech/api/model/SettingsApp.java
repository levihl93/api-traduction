package tunutech.api.model;

import jakarta.persistence.*;
import lombok.Data;

@Table(name = "settingsapp")
@Entity
@Data
public class SettingsApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double majoration=0.0;

}
