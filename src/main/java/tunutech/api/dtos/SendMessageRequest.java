package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tunutech.api.model.MessageType;

@Getter
@Setter
@ToString
public class SendMessageRequest {
    private String chatRoomId;
    private String content;
    private MessageType type = MessageType.TEXT;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
}
