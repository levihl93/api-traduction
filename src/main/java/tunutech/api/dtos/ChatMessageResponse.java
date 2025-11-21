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
public class ChatMessageResponse {
    private Long id;
    private String content;
    private SenderRole senderRole;
    private String senderName;
    private MessageType type;
    private String fileName;
    private String fileUrl;
    private Long SenderId;
    private Long fileSize;
    private LocalDateTime timestamp;
    private Boolean isRead;
}
