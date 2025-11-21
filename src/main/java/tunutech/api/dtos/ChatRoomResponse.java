package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tunutech.api.model.ChatStatus;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
public class ChatRoomResponse {
    private Long id;
    private String roomId;
    private Long projectId;
    private String projectTitle;
    private String clientName;
    private String traducteurName;
    private ChatStatus status;
    private Date createdAt;
    private ChatMessageResponse lastMessage;
    private String userLastMessage;
    private Long unreadCount;
    List<ParticipantsDto> participants;
}
