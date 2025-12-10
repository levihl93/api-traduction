package tunutech.api.dtos;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class AnalyticsDetailedDataDto {
    private LocalDateTime date;

    private Integer traductions;

    private Integer motsTraduits;

    private String languesUtilisees;

    private BigDecimal precision;

    private Integer erreurs;

    private Integer utilisateursActifs;

    private Integer nouveauxClients;

    private Integer projetsCrees;

    private Integer projetsTermines;

    private BigDecimal satisfactionClient;
}
