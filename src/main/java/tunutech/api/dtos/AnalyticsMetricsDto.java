package tunutech.api.dtos;

import lombok.Data;

import java.math.BigDecimal;

@Data

public class AnalyticsMetricsDto {
    private Long totalTraductions;

    private Integer motsParJour;

    private BigDecimal precisionMoyenne;

    private Integer languesActives;

    private BigDecimal croissance;

    private Integer nouveauxClients;

    private Integer totalProjets;

    private Integer projetsTermines;

    private BigDecimal tauxCompletion;

    private Integer utilisateursActifs;

    private Integer erreursTotal;

    private BigDecimal revenus;

    private BigDecimal tempsMoyenTraitement;
}
