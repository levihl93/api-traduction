package tunutech.api.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TypingNotification {
    private String chatRoomId;
    private Long senderId;
    private String senderName;
    private Boolean isTyping;
    private Long timestamp;
}
