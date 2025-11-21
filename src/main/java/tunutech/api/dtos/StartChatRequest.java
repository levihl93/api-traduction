package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartChatRequest {
    private Long clientId;
    private Long projectId;

    // Constructeurs
    public StartChatRequest() {}

    public StartChatRequest(Long clientId) {
        this.clientId = clientId;
    }
}
