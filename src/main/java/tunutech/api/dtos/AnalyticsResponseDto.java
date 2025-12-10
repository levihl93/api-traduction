package tunutech.api.dtos;

import lombok.Data;

import java.util.List;

@Data
public class AnalyticsResponseDto {
    private AnalyticsMetricsDto metrics;

    private List<AnalyticsDetailedDataDto> detailedData;

    private ChartDataDto charts;

    private AnalyticsSummaryDto summary;

    private Boolean success = true;

    private String error;

    // Constructeur de succès
    public static AnalyticsResponseDto success(AnalyticsMetricsDto metrics,
                                               List<AnalyticsDetailedDataDto> detailedData,
                                               ChartDataDto charts,
                                               AnalyticsSummaryDto summary) {
        AnalyticsResponseDto response = new AnalyticsResponseDto();
        response.setMetrics(metrics);
        response.setDetailedData(detailedData);
        response.setCharts(charts);
        response.setSummary(summary);
        response.setSuccess(true);
        return response;
    }

    // Constructeur d'erreur
    public static AnalyticsResponseDto error(String message) {
        AnalyticsResponseDto response = new AnalyticsResponseDto();
        response.setSuccess(false);
        response.setError(message);
        return response;
    }
}
