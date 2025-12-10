package tunutech.api.dtos;

import lombok.Data;
import tunutech.api.model.Client;

import java.time.LocalDateTime;

@Data
public class ActivityClientPeriodeResponseDto {
    private Client client;
    private LocalDateTime data;
}
