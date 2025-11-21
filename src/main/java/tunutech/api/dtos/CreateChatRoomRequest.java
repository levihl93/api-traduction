package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CreateChatRoomRequest {
    private Long clientId;
    private Long traducteurId;
    private Long projectid;
}
