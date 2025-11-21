package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import tunutech.api.Utils.SecurityUtils;
import tunutech.api.dtos.TypingNotification;
import tunutech.api.dtos.WebSocketMessage;
import tunutech.api.model.*;
import tunutech.api.services.ChatMessageService;
import tunutech.api.services.ChatRoomService;
import tunutech.api.services.UserService;

import java.security.Principal;

@Controller
public class WebSocketChatController {
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomService chatRoomService;

    @Autowired
    private UserService userService;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Envoyer un message via WebSocket - CORRIGÉ
    @MessageMapping("/chat.sendMessage")
    public void sendMessage(@Payload WebSocketMessage webSocketMessage, Principal principal) {
        try {
            System.out.println("=== DEBUG WebSocketMessage ===");
            System.out.println("Content: " + webSocketMessage.getContent());
            System.out.println("ChatRoomId: " + webSocketMessage.getChatRoomId());
            System.out.println("SenderId: " + webSocketMessage.getSenderId());
            System.out.println("SenderRole: " + webSocketMessage.getSenderRole());
            System.out.println("Type: " + webSocketMessage.getType());

            System.out.println("=== STEP 1: Récupération utilisateur ===");
            User sender = userService.getByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + principal.getName()));
            System.out.println("✅ Utilisateur trouvé: " + sender.getId() + " - " + sender.getEmail());

            System.out.println("=== STEP 2: Récupération chatroom ===");
            ChatRoom chatRoom = chatRoomService.getChatRoomByRoomId(webSocketMessage.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("ChatRoom non trouvé"));
            System.out.println("✅ ChatRoom trouvé: " + chatRoom.getId());

            System.out.println("=== STEP 3: Vérification accès ===");
            boolean hasAccess = chatRoomService.hasAccessToChatRoom(sender.getId(), webSocketMessage.getChatRoomId());
            System.out.println("✅ Accès vérifié: " + hasAccess);

            if (!hasAccess) {
                throw new AccessDeniedException("Accès non autorisé à cette conversation");
            }

            System.out.println("=== STEP 4: Envoi du message ===");
            ChatMessage savedMessage = chatMessageService.sendTextMessage(
                    chatRoom, sender, webSocketMessage.getContent(), webSocketMessage.getSenderRole()
            );

            String senderName = getSenderName(sender);
            System.out.println("🔍 Nom de l'expéditeur: " + senderName);

            System.out.println("✅ Message sauvegardé: " + savedMessage.getId());
            System.out.println("✅ Role de lexpediteur: " + savedMessage.getSenderRole());

            System.out.println("=== STEP 5: Préparation réponse ===");
            WebSocketMessage response = new WebSocketMessage();
            response.setType(MessageType.MESSAGE);
            response.setChatRoomId(chatRoom.getRoomId());
            response.setContent(savedMessage.getContent());
            response.setSenderRole(savedMessage.getSenderRole());
            response.setSenderName(SecurityUtils.safeGetUserFullName(savedMessage.getUser()));
            response.setSenderId(savedMessage.getUser().getId());
            response.setTimestamp(savedMessage.getTimestamp());
            System.out.println("✅ Réponse préparée: " + response);

            System.out.println("=== STEP 6: Envoi via WebSocket ===");
            messagingTemplate.convertAndSend(
                    "/topic/chatroom/" + webSocketMessage.getChatRoomId(),
                    response
            );
            System.out.println("✅ Message envoyé via WebSocket");

            System.out.println("=== SUCCÈS COMPLET ===");

        } catch (Exception e) {
            System.out.println("=== ERREUR DÉTECTÉE ===");
            System.out.println("Erreur: " + e.getMessage());
            System.out.println("Type d'erreur: " + e.getClass().getName());
            e.printStackTrace(); // ← TRÈS IMPORTANT

            WebSocketMessage errorResponse = new WebSocketMessage();
            errorResponse.setType(MessageType.ERROR);
            errorResponse.setContent("Erreur lors de l'envoi du message: " + e.getMessage());

            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    errorResponse
            );
        }
    }

    // ✅ NOUVELLE METHODE : Récupérer le nom selon le rôle
    private String getSenderName(User user) {
        if (user.getRoleUser() == RoleUser.CLIENT && user.getClient() != null) {
            return user.getClient().getFullName();
        } else if (user.getRoleUser() == RoleUser.TRANSLATOR && user.getTraducteur() != null) {
            return user.getTraducteur().getFullName();
        } else if (user.getRoleUser() == RoleUser.ADMIN) {
            return "Administrateur";
        }

        // Fallback
        return user.getEmail();
    }

    // ✅ CORRECTION : Méthode joinChatRoom
    @MessageMapping("/chat.join")
    public void joinChatRoom(@Payload WebSocketMessage joinMessage, Principal principal) {
        try {
            // ✅ CORRECTION : Récupérer l'utilisateur par email
            User user = userService.getByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + principal.getName()));

            String chatRoomId = joinMessage.getChatRoomId();

            if (!chatRoomService.hasAccessToChatRoom(user.getId(), chatRoomId)) {
                throw new AccessDeniedException("Accès non autorisé");
            }

            // ✅ CORRECTION : Utiliser la méthode sécurisée
            String senderName = safeGetUserFullName(user);
            System.out.println("🔍 Nom sécurisé: " + senderName);

            // Notifier que l'utilisateur a rejoint
            WebSocketMessage response = new WebSocketMessage();
            response.setType(MessageType.USER_JOINED);
            response.setChatRoomId(chatRoomId);
            response.setSenderId(user.getId()); // ✅ Le vrai ID
            response.setSenderName(senderName);

            messagingTemplate.convertAndSend(
                    "/topic/chatroom/" + chatRoomId,
                    response
            );

        } catch (Exception e) {
            WebSocketMessage errorResponse = new WebSocketMessage();
            errorResponse.setType(MessageType.ERROR);
            errorResponse.setContent("Erreur lors de la connexion: " + e.getMessage());

            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    errorResponse
            );
        }
    }

    // ✅ CORRECTION : Méthode markAsRead
    @MessageMapping("/chat.markAsRead")
    public void markAsRead(@Payload WebSocketMessage readMessage, Principal principal) {
        try {
            // ✅ CORRECTION : Récupérer l'utilisateur par email
            User user = userService.getByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + principal.getName()));

            // Utiliser l'ID de l'utilisateur authentifié
            chatMessageService.markMessagesAsRead(readMessage.getChatRoomId(), user);

            // Notifier que les messages ont été lus
            WebSocketMessage response = new WebSocketMessage();
            response.setType(MessageType.READ_RECEIPT);
            response.setChatRoomId(readMessage.getChatRoomId());
            response.setSenderId(user.getId()); // ✅ Le vrai ID

            messagingTemplate.convertAndSend(
                    "/topic/chatroom/" + readMessage.getChatRoomId() + "/read",
                    response
            );

        } catch (Exception e) {
            WebSocketMessage errorResponse = new WebSocketMessage();
            errorResponse.setType(MessageType.ERROR);
            errorResponse.setContent("Erreur lors du marquage comme lu");

            messagingTemplate.convertAndSendToUser(
                    principal.getName(),
                    "/queue/errors",
                    errorResponse
            );
        }
    }

    // ✅ CORRECTION : Méthode typingNotification
    @MessageMapping("/chat.typing")
    public void typingNotification(@Payload TypingNotification receivedTyping, Principal principal) {
        try {
            // ✅ CORRECTION : Récupérer l'utilisateur par email
            User user = userService.getByEmail(principal.getName())
                    .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé: " + principal.getName()));

            if (!chatRoomService.hasAccessToChatRoom(user.getId(), receivedTyping.getChatRoomId())) {
                return;
            }

            TypingNotification typingNotification = new TypingNotification();
            typingNotification.setChatRoomId(receivedTyping.getChatRoomId());
            typingNotification.setSenderId(user.getId()); // ✅ Le vrai ID
            typingNotification.setIsTyping(receivedTyping.getIsTyping());
            typingNotification.setTimestamp(System.currentTimeMillis());

            messagingTemplate.convertAndSend(
                    "/topic/chatroom/" + typingNotification.getChatRoomId() + "/typing",
                    typingNotification
            );

        } catch (Exception e) {
            System.err.println("Erreur typing notification: " + e.getMessage());
        }
    }

    // ✅ AJOUTE cette méthode dans le même controller
    private String safeGetUserFullName(User user) {
        if (user == null) return "Utilisateur inconnu";

        try {
            if (user.getRoleUser() == RoleUser.CLIENT && user.getClient() != null) {
                return user.getClient().getFullName();
            } else if (user.getRoleUser() == RoleUser.TRANSLATOR && user.getTraducteur() != null) {
                return user.getTraducteur().getFullName();
            } else if (user.getRoleUser() == RoleUser.ADMIN) {
                return "Administrateur";
            }
        } catch (Exception e) {
            System.out.println("⚠️ Erreur safeGetUserFullName: " + e.getMessage());
        }
        return user.getEmail() != null ? user.getEmail() : "Utilisateur";
    }
}