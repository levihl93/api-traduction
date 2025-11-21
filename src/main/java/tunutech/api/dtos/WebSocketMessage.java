package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import tunutech.api.model.MessageType;
import tunutech.api.model.SenderRole;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class WebSocketMessage {
    private MessageType type; // MESSAGE, TYPING, READ_RECEIPT
    private String chatRoomId;
    private String content;
    private SenderRole senderRole;
    private String senderName;
    private Long senderId;
    private String fileName;
    private String fileUrl;
    private Long fileSize;
    private LocalDateTime timestamp;
}
