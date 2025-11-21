package tunutech.api.services.implementsServices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tunutech.api.model.*;
import tunutech.api.repositories.ChatRoomRepository;
import tunutech.api.services.ChatRoomService;
import tunutech.api.services.ContratService;
import tunutech.api.services.UserService;
import tunutech.api.Utils.Functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    @Autowired
    private ChatRoomRepository chatRoomRepository;

    @Autowired
    private Functions functions;

    @Autowired
    private UserService userService;


    @Override
    public ChatRoom createOrGetChatRoom(Client client, Traducteur traducteur,Project project) {
        return chatRoomRepository.findByClientAndTraducteurAndProject(client, traducteur,project)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setClient(client);
                    newRoom.setTraducteur(traducteur);
                    newRoom.setProject(project);
                    newRoom.setRoomId(UUID.randomUUID().toString());
                    newRoom.setChatStatus(ChatStatus.ACTIVE);
                    return chatRoomRepository.save(newRoom);
                });
    }

    @Override
    public ChatRoom createAdminClientChatRoom(Client client, Project project, User user) {
        return  this.createChat(client,user,project,ChatStatus.PRE_CONTRACT);
    }
    @Override
    public ChatRoom createAdminClientChatRoomContract(Client client, Project project, User user) {
        return  this.createChat(client,user,project,ChatStatus.CONTRACT);
    }

    @Override
    public ChatRoom createTRaducteurClientChatRoom(Client client, Project project, User user) {
        String roomId;
       roomId=this.CreateCode(client,user,project);
        return chatRoomRepository.findByRoomId(roomId)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);
                    newRoom.setClient(client);
                    newRoom.setProject(project);
                    newRoom.setTraducteur(user.getTraducteur()); // Admin = Traducteur
                    newRoom.setChatStatus(ChatStatus.CONTRACT);
                    return chatRoomRepository.save(newRoom);
                });
    }

    @Override
    public User getUserofChatAndClientAndChatStatuts(Client client, Project project, ChatStatus chatStatus) {
        User user=new User();
        Optional<ChatRoom> chatRoom=findByClientAndChatStatusAndProject(client,project,chatStatus);
        if(chatRoom.isPresent())
        {
            user=userService.getByTraducteur(chatRoom.get().getTraducteur().getId());
        }
        return user;
    }

    @Override
    public ChatRoom createClientAdminChatRoom(Client client, User user,Project project) {
        return  this.createChat(client,user,project,ChatStatus.PRE_CONTRACT);
    }
    @Override
    public ChatRoom createClientAdminChatRoomContract(Client client, User user,Project project) {
        return  this.createChat(client,user,project,ChatStatus.CONTRACT);
    }

    private ChatRoom createChat(Client client,User user,Project project,ChatStatus chatStatus)
    {
        String roomId;
        roomId=this.CreateCode(client,user,project);
        return chatRoomRepository.findByRoomId(roomId)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);
                    newRoom.setClient(client);
                    newRoom.setProject(project);
                    newRoom.setTraducteur(user.getTraducteur()); // Admin = Traducteur
                    newRoom.setChatStatus(chatStatus);
                    return chatRoomRepository.save(newRoom);
                });
    }

    @Override
    public ChatRoom createClientTraducteurChatRoom(Client client, Project project) {
        return null;
    }


    @Override
    public ChatRoom findByClientAndTraducteurAndChatStatus(Client client, Traducteur traducteur, ChatStatus chatStatus) {
        return null;
    }

    @Override
    public Optional<ChatRoom> findByClientAndChatStatusAndProject(Client client, Project project, ChatStatus chatStatus) {
        return chatRoomRepository.findByClientAndChatStatusAndProject(client,chatStatus,project);
    }

    @Override
    public ChatRoom findByClientAndTraducteurAndProjectAndChatStatus(Client client, Traducteur traducteur, Project project, ChatStatus chatStatus) {
        return chatRoomRepository.findByClientAndTraducteurAndProjectAndChatStatus(client,traducteur,project,chatStatus);
    }


    @Override
    public List<ChatRoom> findByClientAndChatStatus(Client client, ChatStatus chatStatus) {
        return chatRoomRepository.findByClientAndChatStatus(client,chatStatus);
    }

    @Override
    public List<ChatRoom> getPreContractChatRooms(User user) {
        if (user.getTraducteur()!=null) {
            return chatRoomRepository.findByTraducteurAndChatStatus(
                    user.getTraducteur(), ChatStatus.PRE_CONTRACT);
        }
        return new ArrayList<>();
    }

    @Override
    public String CreateCode(Client client,User user,Project project) {
        String code;
        String code1;
        do{
            code1=functions.generateComplexPassword(8,false);
            code="CH-"+client.getId()+"-"+user.getId()+code1+"-"+project.getId();
        } while (chatRoomRepository.findByRoomId(code).isPresent());

        return code;
    }

    @Override
    public List<ChatRoom> getActiveChatRooms(User user) {
        if (user.getClient()!=null) {
            return chatRoomRepository.findByClientAndChatStatus(
                    user.getClient(), ChatStatus.ACTIVE);
        } else if (user.getTraducteur()!=null) {
            return chatRoomRepository.findByTraducteurAndChatStatus(
                    user.getTraducteur(), ChatStatus.ACTIVE);
        }
        return new ArrayList<>();
    }

    @Override
    public List<ChatRoom> getUserChatRooms(User user) {
        if(user.getClient()!=null)
        {
            return this.getChatRoomsByClient(user.getClient());
        }else {
            if(user.getTraducteur()!=null)
            {
                return this.getChatRoomsByTraducteur(user.getTraducteur());
            }
        }
        return new ArrayList<>();
    }

    @Override
    public Optional<ChatRoom> getChatRoomByRoomId(String roomId) {
        return chatRoomRepository.findByRoomId(roomId);
    }

    @Override
    public List<ChatRoom> getChatRoomsByClient(Client client) {
        return chatRoomRepository.findByClient(client);
    }

    @Override
    public List<ChatRoom> getChatRoomsByTraducteur(Traducteur traducteur) {
        return chatRoomRepository.findByTraducteur(traducteur);
    }

    @Override
    public boolean hasAccessToChatRoom(String username, String chatRoomId) {
        try {
            Long userId = Long.parseLong(username);
            return hasAccessToChatRoom(userId, chatRoomId);
        } catch (NumberFormatException e) {
            return hasAccessToChatRoomByUsername(username, chatRoomId);
        }
    }

    @Override
    public boolean hasAccessToChatRoom(Long userId, String chatRoomId) {
        try {
            System.out.println("🔍 Vérification accès - userId: " + userId + ", chatRoomId: " + chatRoomId);

            Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByRoomId(chatRoomId);

            if (chatRoomOpt.isPresent()) {
                ChatRoom chatRoom = chatRoomOpt.get();

                // Récupérer l'utilisateur complet
                User user = userService.getById(userId)
                        .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + userId));

                System.out.println("🔍 User: " + user.getEmail() + ", Role: " + user.getRoleUser());

                // Vérifier l'accès selon le rôle
                boolean hasAccess = false;

                if (user.getRoleUser() == RoleUser.CLIENT) {
                    // Pour un client, vérifier s'il correspond au client de la chatroom
                    hasAccess = chatRoom.getClient() != null &&
                            user.getClient() != null &&
                            chatRoom.getClient().getId().equals(user.getClient().getId());
                    System.out.println("🔍 Accès client: " + hasAccess);

                } else if (user.getRoleUser() == RoleUser.TRANSLATOR) {
                    // Pour un traducteur, vérifier s'il correspond au traducteur de la chatroom
                    hasAccess = chatRoom.getTraducteur() != null &&
                            user.getTraducteur() != null &&
                            chatRoom.getTraducteur().getId().equals(user.getTraducteur().getId());
                    System.out.println("🔍 Accès traducteur: " + hasAccess);

                } else if (user.getRoleUser() == RoleUser.ADMIN) {
                    // L'admin a accès à tout
                    hasAccess = true;
                    System.out.println("🔍 Accès admin: " + hasAccess);
                }

                System.out.println("🔍 Résultat accès final: " + hasAccess);
                return hasAccess;
            }

            System.out.println("🔍 ChatRoom non trouvé");
            return false;

        } catch (Exception e) {
            System.out.println("❌ Erreur dans hasAccessToChatRoom: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean hasAccessToChatRoomByUsername(String username, String chatRoomId) {
        Optional<ChatRoom> chatRoomOpt = chatRoomRepository.findByRoomId(chatRoomId);

        if (chatRoomOpt.isPresent()) {
            ChatRoom chatRoom = chatRoomOpt.get();

            // Vérifier si l'utilisateur est le client (par email)
            if (chatRoom.getClient() != null) {
                Client client = chatRoom.getClient();
                boolean isClient = username.equals(client.getEmail());
                if (isClient) return true;
            }

            // Vérifier si l'utilisateur est le traducteur (par email)
            if (chatRoom.getTraducteur() != null) {
                Traducteur traducteur = chatRoom.getTraducteur();
                boolean isTranslator = username.equals(traducteur.getEmail());
                return isTranslator;
            }
        }

        return false;
    }

    @Override
    public List<ChatRoom> getChatRoomsForUser(Long userId) {
        List<ChatRoom> clientRooms = chatRoomRepository.findByClientId(userId);
        List<ChatRoom> translatorRooms = chatRoomRepository.findActiveChatRoomsByTraducteur(userId);

        List<ChatRoom> allRooms = new ArrayList<>();
        allRooms.addAll(clientRooms);
        allRooms.addAll(translatorRooms);

        return allRooms;
    }

    @Override
    public List<ChatRoom> getActiveChatRoomsForUser(Long userId) {
        List<ChatRoom> userRooms = getChatRoomsForUser(userId);
        return userRooms.stream()
                // Filtrer seulement les chatrooms actives
                .filter(chatRoom -> chatRoom.getChatStatus() == ChatStatus.ACTIVE)
                .collect(Collectors.toList());
    }

    @Override
    public boolean existsByClientAndTraducteur(Client client, Traducteur traducteur) {
        return chatRoomRepository.existsByClientAndTraducteur(client, traducteur);
    }
}
