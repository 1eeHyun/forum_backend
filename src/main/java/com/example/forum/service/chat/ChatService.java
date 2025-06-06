package com.example.forum.service.chat;

import com.example.forum.dto.chat.ChatMessageDTO;

import java.util.List;

public interface ChatService {

    String getOrCreateRoomId(Long user1Id, Long user2Id);
    void saveMessage(ChatMessageDTO dto);
    List<ChatMessageDTO> getMessage(String roomId, String currUsername);
}
