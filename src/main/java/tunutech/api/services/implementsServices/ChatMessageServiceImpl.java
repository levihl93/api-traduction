package tunutech.api.services.implementsServices;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.model.*;
import tunutech.api.repositories.ChatMessageRepository;
import tunutech.api.services.ChatMessageService;
import tunutech.api.services.ChatRoomService;

import java.util.List;

@Service
@Transactional
public class ChatMessageServiceImpl implements ChatMessageService {
    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatRoomService chatRoomService;
    @Override
    public ChatMessage sendTextMessage(ChatRoom chatRoom, User sender, String content, SenderRole senderRole) {
        System.out.println("🔍 Structure User:");
        System.out.println("  - ID: " + sender.getId());
        System.out.println("  - Email: " + sender.getEmail());
        System.out.println("  - Role: " + sender.getRoleUser());
        System.out.println("  - Client: " + (sender.getClient() != null ? sender.getClient().getId() : "null"));
        System.out.println("  - Traducteur: " + (sender.getTraducteur() != null ? sender.getTraducteur().getId() : "null"));


        System.out.println("📝 sendTextMessage - sender role: " + sender.getRoleUser());
        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setUser(sender);
        message.setSenderRole(senderRole);
        message.setContent(content);
        message.setType(MessageType.TEXT);
        message.setIsRead(false);

        System.out.println("📝 Sauvegarde du message...");
        ChatMessage savedMessage = chatMessageRepository.save(message);
        System.out.println("✅ Message sauvegardé avec ID: " + savedMessage.getId());

        return savedMessage;
    }

    @Override
    public ChatMessage sendFileMessage(ChatRoom chatRoom, User sender, String fileName, String fileUrl, Long fileSize, SenderRole senderRole) {
        ChatMessage message = new ChatMessage();
        message.setChatRoom(chatRoom);
        message.setUser(sender);
        message.setSenderRole(senderRole);
        message.setContent("Fichier: " + fileName);
        message.setType(MessageType.FILE);
        message.setFileName(fileName);
        message.setFileUrl(fileUrl);
        message.setFileSize(fileSize);
        message.setIsRead(false);

        return chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessage> getChatHistory(String chatRoomId) {
        System.out.println(chatRoomId);
        ChatRoom chatRoom = chatRoomService.getChatRoomByRoomId(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom non trouvé"));
        return chatMessageRepository.findByChatRoomOrderByTimestampAsc(chatRoom);
    }

    @Override
    public void markMessagesAsRead(String chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomService.getChatRoomByRoomId(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom non trouvé"));

        List<ChatMessage> unreadMessages = chatMessageRepository.findByChatRoomAndIsReadFalse(chatRoom);

        for (ChatMessage message : unreadMessages) {
            if (!message.getUser().equals(user)) {
                message.setIsRead(true);
            }
        }
        chatMessageRepository.saveAll(unreadMessages);
    }

    @Override
    public ChatMessage findTopByChatRoomOrderByTimestampDesc(ChatRoom chatRoom) {
        return  chatMessageRepository.findTopByChatRoomOrderByTimestampDesc(chatRoom);
    }

    @Override
    public Long getUnreadCount(String chatRoomId, User user) {
        ChatRoom chatRoom = chatRoomService.getChatRoomByRoomId(chatRoomId)
                .orElseThrow(() -> new RuntimeException("ChatRoom non trouvé"));
        return chatMessageRepository.countUnreadMessages(chatRoom, user);
    }
}
