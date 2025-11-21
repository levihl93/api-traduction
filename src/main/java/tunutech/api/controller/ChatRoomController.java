package tunutech.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tunutech.api.Utils.SecurityUtils;
import tunutech.api.dtos.*;
import tunutech.api.model.*;
import tunutech.api.repositories.ProjectRepository;
import tunutech.api.repositories.ProjetTraducteurRepository;
import tunutech.api.services.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatRoomController {
    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    ProjetService projetService;

    @Autowired
    private ClientService clientService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjetTraducteurRepository projetTraducteurRepository;

    @Autowired
    private final SimpMessagingTemplate messagingTemplate;


    @Autowired
    private TraducteurService traducteurService;

    @Autowired
    private ContratService contratService;

    // Créer ou récupérer une chatroom
    @PostMapping
    public ResponseEntity<?> createOrGetChatRoom(@RequestBody CreateChatRoomRequest request) {
        try {
            Client client = clientService.getUnique(request.getClientId());
            Traducteur traducteur = traducteurService.getUnique(request.getTraducteurId());
            Project project=projetService.getUniquebyId(request.getProjectid());

            ChatRoom chatRoom = chatRoomService.createOrGetChatRoom(client, traducteur,project);
            return ResponseEntity.ok(mapToChatRoomResponse(chatRoom));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    // Récupérer toutes les chatrooms d'un utilisateur
    @GetMapping("/my-rooms")
    public ResponseEntity<List<ChatRoomResponse>> getUserChatRooms(@AuthenticationPrincipal User user) {
        List<ChatRoom> chatRooms = chatRoomService.getUserChatRooms(user);
        List<ChatRoomResponse> responses = chatRooms.stream()
                .map(this::mapToChatRoomResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/getChatRoom/{roomId}")
    public ResponseEntity<?> getChatRooms(@PathVariable String roomId) {
        Optional<ChatRoom> chatRooms = chatRoomService.getChatRoomByRoomId(roomId);
        return ResponseEntity.ok(chatRooms);
    }

    // Méthode de mapping
    private ChatRoomResponse mapToChatRoomResponse(ChatRoom chatRoom) {
        ChatRoomResponse response = new ChatRoomResponse();

        response.setId(chatRoom.getId());
        response.setProjectId(chatRoom.getProject().getId());
        response.setProjectTitle(chatRoom.getProject().getTitle());
        response.setRoomId(chatRoom.getRoomId());
        response.setClientName(chatRoom.getClient().getFullName());
        response.setTraducteurName(chatRoom.getTraducteur().getFullName());
        response.setStatus(chatRoom.getChatStatus());
        response.setCreatedAt(chatRoom.getCreated_At());
        // Dernier message
        Optional<ChatMessage> lastMessage = Optional.ofNullable(chatMessageService.findTopByChatRoomOrderByTimestampDesc(chatRoom));

        lastMessage.ifPresent(message -> response.setLastMessage(mapToMessageResponse(message)));
        if(lastMessage.isPresent())
        {
            response.setUserLastMessage(lastMessage.get().getUser().getFullName());
        }
        // Compter messages non lus (à implémenter selon ta logique)
        Integer unreadCount;
        User user=userService.getByClient(chatRoom.getClient().getId());
        unreadCount= Math.toIntExact(chatMessageService.getUnreadCount(chatRoom.getRoomId(), user));
        response.setUnreadCount(Long.valueOf(unreadCount));
        List<ParticipantsDto> participants = new ArrayList<>();

        // ✅ Participant 1 - Client
        ParticipantsDto participantsDto=new ParticipantsDto();
        participantsDto.setName(chatRoom.getClient().getFullName());
        participantsDto.setRole("client");
        participantsDto.setId(chatRoom.getClient().getId());
        participantsDto.setAvatar("/avatars/jean.jpg");

        participants.add(participantsDto);

        // ✅ Participant 2 - Traducteur
        ParticipantsDto participantsDto1=new ParticipantsDto();
        participantsDto1.setName(chatRoom.getTraducteur().getFullName());
        participantsDto1.setRole("traducteur");
        participantsDto1.setId(chatRoom.getTraducteur().getId());
        participantsDto1.setAvatar("/avatars/jean.jpg");

        participants.add(participantsDto1);


        response.setParticipants(participants);
        return response;
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage message) {
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setSenderRole(message.getSenderRole());
        response.setSenderName(SecurityUtils.safeGetUserFullName(message.getUser()));
        response.setType(message.getType());
        response.setFileName(message.getFileName());
        response.setFileUrl(message.getFileUrl());
        response.setFileSize(message.getFileSize());
        response.setTimestamp(message.getTimestamp());
        response.setIsRead(message.getIsRead());
        return response;
    }

    @GetMapping("/active")
    public ResponseEntity<List<ChatRoomResponse>> getActiveChatRooms(@AuthenticationPrincipal User user) {
        List<ChatRoom> chatRooms = chatRoomService.getActiveChatRooms(user);
        List<ChatRoomResponse> responses = chatRooms.stream()
                .map(this::mapToChatRoomResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/pre-contract")
    public ResponseEntity<List<ChatRoomResponse>> getPreContractChatRooms(@AuthenticationPrincipal User user) {
        List<ChatRoom> chatRooms = chatRoomService.getPreContractChatRooms(user);
        List<ChatRoomResponse> responses = chatRooms.stream()
                .map(this::mapToChatRoomResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    //Chat initie par ladmin  vers le client avant un contrat
    @PostMapping("/start-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> startAdminChat(
            @RequestBody StartChatRequest request,
            @AuthenticationPrincipal User admin) {

        try {
            Client client = clientService.getUnique(request.getClientId());
            Project project=projetService.getUniquebyId(request.getProjectId());
            // Vérifier si une chatroom existe déjà
            Optional<ChatRoom> existingRoom = Optional.ofNullable(chatRoomService.findByClientAndTraducteurAndProjectAndChatStatus(
                    client, admin.getTraducteur(), project,ChatStatus.PRE_CONTRACT
            ));



            if (existingRoom.isPresent()) {
                existingRoom.get().setClient(client);
                existingRoom.get().setProject(project);
                existingRoom.get().setTraducteur(admin.getTraducteur());
                return ResponseEntity.ok(mapToChatRoomResponse(existingRoom.get()));
            }

            // Créer une nouvelle chatroom pré-contrat
            ChatRoom newRoom = chatRoomService.createAdminClientChatRoom(client, project,admin);

            messagingTemplate.convertAndSend(
                    "/topic/user/" + client.getId(),
                    new ChatNotification("Nouvelle conversation",
                            "L'admin a démarré une conversation",
                            newRoom.getId())
            );
            return ResponseEntity.ok(mapToChatRoomResponse(newRoom));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    //Chat initie par ladmin  vers le client apres un contrat
    @PostMapping("/start-admin-contract")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> startAdminChatContract(
            @RequestBody StartChatRequest request,
            @AuthenticationPrincipal User admin) {

        try {
            Client client = clientService.getUnique(request.getClientId());
            Project project=projetService.getUniquebyId(request.getProjectId());
            // Vérifier si une chatroom existe déjà
            Optional<ChatRoom> existingRoom = Optional.ofNullable(chatRoomService.findByClientAndTraducteurAndProjectAndChatStatus(
                    client, admin.getTraducteur(), project,ChatStatus.CONTRACT
            ));



            if (existingRoom.isPresent()) {
                existingRoom.get().setClient(client);
                existingRoom.get().setProject(project);
                existingRoom.get().setTraducteur(admin.getTraducteur());
                return ResponseEntity.ok(mapToChatRoomResponse(existingRoom.get()));
            }

            // Créer une nouvelle chatroom contrat
            ChatRoom newRoom = chatRoomService.createAdminClientChatRoomContract(client, project,admin);

            messagingTemplate.convertAndSend(
                    "/topic/user/" + client.getId(),
                    new ChatNotification("Nouvelle conversation",
                            "L'admin a démarré une conversation",
                            newRoom.getId())
            );
            return ResponseEntity.ok(mapToChatRoomResponse(newRoom));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    //Chat initie par un traducteur apres que le projet lui soit attribue
    @PostMapping("/startTranslator")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> startTranslatorClientChat(
            @RequestBody StartChatRequest request,
            @AuthenticationPrincipal User admin) {

        try {
            Client client = clientService.getUnique(request.getClientId());
            Project project=projetService.getUniquebyId(request.getProjectId());
            // Vérifier si une chatroom existe déjà
            Optional<ChatRoom> existingRoom = Optional.ofNullable(chatRoomService.findByClientAndTraducteurAndProjectAndChatStatus(
                    client, admin.getTraducteur(), project,ChatStatus.CONTRACT
            ));

            if (existingRoom.isPresent()) {
                existingRoom.get().setClient(client);
                existingRoom.get().setProject(project);
                existingRoom.get().setTraducteur(admin.getTraducteur());
                return ResponseEntity.ok(mapToChatRoomResponse(existingRoom.get()));
            }

            // Créer une nouvelle chatroom Contrat
            ChatRoom newRoom = chatRoomService.createTRaducteurClientChatRoom(client, project,admin);

            messagingTemplate.convertAndSend(
                    "/topic/user/" + client.getId(),
                    new ChatNotification("Nouvelle conversation",
                            "Le Traducteur a démarré une conversation",
                            newRoom.getId())
            );
            return ResponseEntity.ok(mapToChatRoomResponse(newRoom));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    // Endpoints pour le client
    @GetMapping("/client/active")
    public ResponseEntity<List<ChatRoomResponse>> getClientActiveChatRooms(@AuthenticationPrincipal Client client) {
        List<ChatRoom> chatRooms = chatRoomService.getActiveChatRooms(userService.getByClient(client.getId()));
        return ResponseEntity.ok(chatRooms.stream()
                .map(this::mapToChatRoomResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/client/pre-contract")
    public ResponseEntity<List<ChatRoomResponse>> getClientPreContractChatRooms(@AuthenticationPrincipal Client client) {
        List<ChatRoom> chatRooms = chatRoomService.findByClientAndChatStatus(client, ChatStatus.PRE_CONTRACT);
        return ResponseEntity.ok(chatRooms.stream()
                .map(this::mapToChatRoomResponse)
                .collect(Collectors.toList()));
    }


    //Tchat initit par le client avec ladmin avant le contrat
    @PostMapping("/client/start-chat/project")
    public ResponseEntity<?> clientStartChat(@RequestBody ChatStartClientPrecontratDTO chatStartClientPrecontratDTO) {
        try {
            Client client = clientService.getUnique(chatStartClientPrecontratDTO.getClientId());
            Project project = projetService.getUniquebyId(chatStartClientPrecontratDTO.getProjectId());

            User user = new User();
            user = chatRoomService.getUserofChatAndClientAndChatStatuts(client, project, ChatStatus.PRE_CONTRACT);
            if (user.getId() == null) {
               return ResponseEntity.badRequest().body("Vous ne pouvez pas initier la conversation");
            }


            // Vérifier si une chatroom existe déjà
            Optional<ChatRoom> existingRoom = Optional.ofNullable(chatRoomService.findByClientAndTraducteurAndChatStatus(
                    client, user.getTraducteur(), ChatStatus.PRE_CONTRACT
            ));

            if (existingRoom.isPresent()) {
                existingRoom.get().setClient(client);
                existingRoom.get().setProject(project);
                existingRoom.get().setTraducteur(user.getTraducteur());
                return ResponseEntity.ok(mapToChatRoomResponse(existingRoom.get()));
            }

            // Créer une nouvelle chatroom pré-contrat
            ChatRoom newRoom = chatRoomService.createClientAdminChatRoom(client, user, project);

            messagingTemplate.convertAndSend(
                    "/topic/user/" + client.getId(),
                    new ChatNotification("Nouvelle conversation",
                            "Le Client a démarré une conversation",
                            newRoom.getId())
            );
            return ResponseEntity.ok(mapToChatRoomResponse(newRoom));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    //Tchat initie par le client avec ladmin apres le contrat
    @PostMapping("/client/start-chat-contract/project")
    public ResponseEntity<?> clientStartChatContract(@RequestBody ChatStartClientPrecontratDTO chatStartClientPrecontratDTO) {
        try {
            Client client = clientService.getUnique(chatStartClientPrecontratDTO.getClientId());
            Project project = projetService.getUniquebyId(chatStartClientPrecontratDTO.getProjectId());

            User user = new User();
            user = chatRoomService.getUserofChatAndClientAndChatStatuts(client, project, ChatStatus.CONTRACT);

            if (user == null) {
                //Récuperer le user qui a générer le contrat et lui attribuer le tchat
                Contrat contrat = contratService.getofProject(project.getCode());
                user = contrat.getUser();
            }
            User finalUser = user;


            // Vérifier si une chatroom existe déjà
            Optional<ChatRoom> existingRoom = Optional.ofNullable(chatRoomService.findByClientAndTraducteurAndChatStatus(
                    client, finalUser.getTraducteur(), ChatStatus.CONTRACT
            ));


            if (existingRoom.isPresent()) {
                existingRoom.get().setClient(client);
                existingRoom.get().setProject(project);
                existingRoom.get().setTraducteur(finalUser.getTraducteur());
                return ResponseEntity.ok(mapToChatRoomResponse(existingRoom.get()));
            }

            // Créer une nouvelle chatroom pré-contrat
            ChatRoom newRoom = chatRoomService.createClientAdminChatRoom(client, finalUser, project);

            messagingTemplate.convertAndSend(
                    "/topic/user/" + client.getId(),
                    new ChatNotification("Nouvelle conversation",
                            "Le Client a démarré une conversation",
                            newRoom.getId())
            );
            return ResponseEntity.ok(mapToChatRoomResponse(newRoom));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    //Tchat initie par le client avec le traducteur attriubue au projet
    @PostMapping("/client/start-chat-translator/project")
    public ResponseEntity<?> clientStartChatTranslator(@RequestBody ChatStartClientPrecontratDTO chatStartClientPrecontratDTO) {
        try {
            Client client = clientService.getUnique(chatStartClientPrecontratDTO.getClientId());
            Project project = projetService.getUniquebyId(chatStartClientPrecontratDTO.getProjectId());

            Optional <ProjetTraducteur> projetTraducteur = projetTraducteurRepository.findByProjectId(chatStartClientPrecontratDTO.getProjectId());

            if(projetTraducteur.isPresent())
            {
                User finalUser = projetTraducteur.get().getUser();


                // Vérifier si une chatroom existe déjà
                Optional<ChatRoom> existingRoom = Optional.ofNullable(chatRoomService.findByClientAndTraducteurAndChatStatus(
                        client, finalUser.getTraducteur(), ChatStatus.CONTRACT
                ));


                if (existingRoom.isPresent()) {
                    existingRoom.get().setClient(client);
                    existingRoom.get().setProject(project);
                    existingRoom.get().setTraducteur(finalUser.getTraducteur());
                    return ResponseEntity.ok(mapToChatRoomResponse(existingRoom.get()));
                }

                // Créer une nouvelle chatroom pré-contrat
                // Créer une nouvelle chatroom Contrat
                ChatRoom newRoom = chatRoomService.createTRaducteurClientChatRoom(client, project,finalUser);

                messagingTemplate.convertAndSend(
                        "/topic/user/" + client.getId(),
                        new ChatNotification("Nouvelle conversation",
                                "Le Client a démarré une conversation",
                                newRoom.getId())
                );
                return ResponseEntity.ok(mapToChatRoomResponse(newRoom));

            }return ResponseEntity.badRequest().body("Erreur: ");

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }
}
