package com.example.forum.controller.chat.websocket;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.helper.chat.ChatMessageBuilder;
import com.example.forum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpAttributesContextHolder;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageBuilder chatMessageBuilder;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDTO message, Message<?> rawMessage) {

        UserDetails userDetails = (UserDetails)
                SimpAttributesContextHolder.currentAttributes().getAttribute("user");

        String senderUsername = userDetails.getUsername();
        message.setSenderUsername(senderUsername);

        ChatMessageDTO enriched = chatMessageBuilder.enrichSenderProfile(message);
        ChatMessageDTO saved = chatService.saveMessage(enriched);

        messagingTemplate.convertAndSend("/topic/chat." + saved.getRoomId(), saved);
    }
}
