package tunutech.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tunutech.api.Utils.SecurityUtils;
import tunutech.api.dtos.ChatMessageResponse;
import tunutech.api.dtos.SendMessageRequest;
import tunutech.api.model.*;
import tunutech.api.services.ChatMessageService;
import tunutech.api.services.ChatRoomService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat/messages")
@CrossOrigin(origins = "*")
public class ChatMessageController {
    @Autowired
    private ChatMessageService chatMessageService;

    @Autowired
    private ChatRoomService chatRoomService;


    // Envoyer un message
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request,
                                         @AuthenticationPrincipal User user) {
        try {
            ChatRoom chatRoom = chatRoomService.getChatRoomByRoomId(request.getChatRoomId())
                    .orElseThrow(() -> new RuntimeException("ChatRoom non trouvé"));

            // Déterminer le senderRole
            SenderRole senderRole = user.getClient()!=null ? SenderRole.CLIENT : SenderRole.TRANSLATOR;

            ChatMessage message;
            if (request.getType() == MessageType.TEXT) {
                message = chatMessageService.sendTextMessage(chatRoom, user, request.getContent(), senderRole);
            } else {
                message = chatMessageService.sendFileMessage(chatRoom, user, request.getFileName(),
                        request.getFileUrl(), request.getFileSize(), senderRole);
            }

            return ResponseEntity.ok(mapToMessageResponse(message));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    // Récupérer l'historique d'une chatroom
    @GetMapping("/history/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponse>> getChatHistory(@PathVariable String chatRoomId) {
        List<ChatMessage> messages = chatMessageService.getChatHistory(chatRoomId);
        List<ChatMessageResponse> responses = messages.stream()
                .map(this::mapToMessageResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // Marquer les messages comme lus
    @PutMapping("/mark-read/{chatRoomId}")
    public ResponseEntity<?> markMessagesAsRead(@PathVariable String chatRoomId,
                                                @AuthenticationPrincipal User user) {
        try {
            chatMessageService.markMessagesAsRead(chatRoomId, user);
            return ResponseEntity.ok("Messages marqués comme lus");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur: " + e.getMessage());
        }
    }

    private ChatMessageResponse mapToMessageResponse(ChatMessage message) {
        // Même méthode que dans ChatRoomController
        // (Tu peux la mettre dans une classe utilitaire)
        ChatMessageResponse response = new ChatMessageResponse();
        response.setId(message.getId());
        response.setSenderId(message.getUser().getId());
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
}
