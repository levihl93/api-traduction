package tunutech.api.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AnalyticsSummaryDto {
    public enum DataStatus {
        COMPLET,
        PARTIEL,
        ESTIME
    }

    private String periode;

    private LocalDateTime dateDebut;

    private LocalDateTime dateFin;

    private LocalDateTime dernierRefresh;

    private Integer totalPoints;

    private DataStatus statut;

    private String message;

    private Long generationTimeMs;
}
