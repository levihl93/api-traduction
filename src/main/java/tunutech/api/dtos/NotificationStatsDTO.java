package tunutech.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationStatsDTO {
    private long total;
    private long unread;
    private long read;

    public double getReadPercentage() {
        return total > 0 ? (read * 100.0) / total : 0;
    }
}
