package tunutech.api.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AnalyticsRequestDto {
    @NotNull(message = "La période est obligatoire")
    @Pattern(regexp = "24h|7j|30j|3m", message = "La période doit être: 24h, 7j, 30j ou 3m")
    private String period;

    private LocalDate startDate;

    private LocalDate endDate;

    private String timezone = "UTC";
}
