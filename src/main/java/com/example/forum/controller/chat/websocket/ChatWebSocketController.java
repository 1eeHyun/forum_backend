package com.example.forum.controller.chat.websocket;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(ChatMessageDTO message) {
        chatService.saveMessage(message);
        messagingTemplate.convertAndSend("/topic/chat." + message.getRoomId(), message); // 실시간 전송
    }
}
