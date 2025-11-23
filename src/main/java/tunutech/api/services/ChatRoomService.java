package tunutech.api.services;

import tunutech.api.model.*;

import java.util.List;
import java.util.Optional;

public interface ChatRoomService {
    ChatRoom createOrGetChatRoom(Client client, Traducteur traducteur,Project project);
    ChatRoom createAdminClientChatRoom(Client client, Project project,User admin);
    ChatRoom createAdminClientChatRoomContract(Client client, Project project,User admin);
    ChatRoom createAdminTraducteurProject(User admin, Project project,Traducteur traducteur);
    Optional <ChatRoom>  findByAdminTraducteurProject(User admin, Project project,Traducteur traducteur);
    ChatRoom createTRaducteurClientChatRoom(Client client, Project project,User user);
    User getUserofChatAndClientAndChatStatuts(Client client,Project project,ChatStatus chatStatus);
    ChatRoom createClientAdminChatRoom(Client client,User user,Project project);
    ChatRoom createClientAdminChatRoomContract(Client client,User user,Project project);
    ChatRoom createClientTraducteurChatRoom(Client client,Project project);
    ChatRoom findByClientAndTraducteurAndChatStatus(Client client,Traducteur traducteur,ChatStatus chatStatus);
    Optional<ChatRoom> findByClientAndChatStatusAndProject(Client client,Project project,ChatStatus chatStatus);
    ChatRoom findByClientAndTraducteurAndProjectAndChatStatus(Client client,Traducteur traducteur,Project project,ChatStatus chatStatus);
    List<ChatRoom> findByClientAndChatStatus(Client client,ChatStatus chatStatus);
    public List<ChatRoom> getPreContractChatRooms(User user);
    String CreateCode(Client client,User user,Project project);
    public List<ChatRoom> getActiveChatRooms(User user);
    List<ChatRoom> getUserChatRooms(User user);
    Optional<ChatRoom> getChatRoomByRoomId(String roomId);
    List<ChatRoom> getChatRoomsByClient(Client client);
    List<ChatRoom> getChatRoomsByTraducteur(Traducteur traducteur);

    boolean hasAccessToChatRoom(String username, String chatRoomId);
    boolean hasAccessToChatRoom(Long userId, String chatRoomId);
    boolean hasAccessToChatRoomByUsername(String username, String chatRoomId);
    List<ChatRoom> getChatRoomsForUser(Long userId);
    List<ChatRoom> getActiveChatRoomsForUser(Long userId);

    boolean existsByClientAndTraducteur(Client client, Traducteur traducteur);
}
