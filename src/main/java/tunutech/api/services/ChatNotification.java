package tunutech.api.services;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ChatNotification {
    private String title;
    private String message;
    private Long chatRoomId;

    public ChatNotification(String title, String message, Long chatRoomId) {
        this.title = title;
        this.message = message;
        this.chatRoomId = chatRoomId;
    }
}
