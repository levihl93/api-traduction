package tunutech.api.services;

import tunutech.api.model.ChatMessage;
import tunutech.api.model.ChatRoom;
import tunutech.api.model.SenderRole;
import tunutech.api.model.User;

import java.util.List;

public interface ChatMessageService {
    ChatMessage sendTextMessage(ChatRoom chatRoom, User sender, String content, SenderRole senderRole);
    ChatMessage sendFileMessage(ChatRoom chatRoom, User sender, String fileName,
                                String fileUrl, Long fileSize, SenderRole senderRole);
    List<ChatMessage> getChatHistory(String chatRoomId);
    void markMessagesAsRead(String chatRoomId, User user);

    ChatMessage findTopByChatRoomOrderByTimestampDesc(ChatRoom chatRoom);
    Long getUnreadCount(String chatRoomId, User user);
}
