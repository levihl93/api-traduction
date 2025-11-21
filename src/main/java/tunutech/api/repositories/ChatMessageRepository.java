package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tunutech.api.model.ChatMessage;
import tunutech.api.model.ChatRoom;
import tunutech.api.model.User;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomOrderByTimestampAsc(ChatRoom chatRoom);

    List<ChatMessage> findByChatRoomAndIsReadFalse(ChatRoom chatRoom);

    @Query("SELECT COUNT(cm) FROM ChatMessage cm WHERE cm.chatRoom = :chatRoom AND cm.isRead = false AND cm.user != :user")
    Long countUnreadMessages(@Param("chatRoom") ChatRoom chatRoom, @Param("user") User user);

    ChatMessage findTopByChatRoomOrderByTimestampDesc(ChatRoom chatRoom);
}
