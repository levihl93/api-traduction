package tunutech.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tunutech.api.model.*;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByClientAndTraducteur(Client client, Traducteur traducteur);
    Optional<ChatRoom> findByClientAndTraducteurAndProject(Client client, Traducteur traducteur, Project project);

    List<ChatRoom> findByClient(Client client);

    List<ChatRoom> findByTraducteur(Traducteur traducteur);

    Optional<ChatRoom> findByRoomId(String roomId);

    boolean existsByClientAndTraducteur(Client client, Traducteur traducteur);

    List<ChatRoom> findByTraducteurAndChatStatus(Traducteur user, ChatStatus preContract);
    Optional <ChatRoom> findByClientAndChatStatusAndProject(Client client,ChatStatus preContract,Project project);

    List<ChatRoom> findByClientAndChatStatus(Client user, ChatStatus active);

    ChatRoom findByClientAndTraducteurAndChatStatus(Client client, Traducteur traducteur, ChatStatus chatStatus);
    ChatRoom findByClientAndTraducteurAndProjectAndChatStatus(Client client, Traducteur traducteur, Project project,ChatStatus chatStatus);

    List<ChatRoom> findByClientId(Long userId);

    // Vérifier l'accès d'un utilisateur
    @Query("SELECT COUNT(cr) > 0 FROM ChatRoom cr WHERE cr.id = :chatRoomId AND " +
            "(cr.client.id = :userId OR cr.traducteur.id = :userId)")
    boolean userHasAccessToChatRoom(@Param("chatRoomId") Long chatRoomId,
                                    @Param("userId") Long userId);

    // Méthodes pour les recherches courantes
    @Query("SELECT cr FROM ChatRoom cr WHERE cr.client.id = :userId AND cr.chatStatus = 'ACTIVE'")
    List<ChatRoom> findActiveChatRoomsByClient(@Param("userId") Long userId);

    @Query("SELECT cr FROM ChatRoom cr WHERE cr.traducteur.id = :userId AND cr.chatStatus = 'ACTIVE'")
    List<ChatRoom> findActiveChatRoomsByTraducteur(@Param("userId") Long userId);
}
