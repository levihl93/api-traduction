package tunutech.api.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ChartDataDto {
    @Data
    public static class ChartItem {
        private String name;
        private Number value;
        private String category;
        private String color;
    }

    @Data
    public static class PerformanceTrendItem {
        private String date;
        private Number performance;
        private Number target;
    }

    private List<ChartItem> languesRepartition;

    private List<ChartItem> topProjets;

    private List<PerformanceTrendItem> performanceTrend;

    private List<ChartItem> typesDocuments;

    private List<ChartItem> activiteHoraire;
}
