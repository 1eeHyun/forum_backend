package com.example.forum.service.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.dto.chat.ChatRoomDTO;

import java.util.List;

public interface ChatService {

    String getOrCreateRoomId(String user1username, String user2username);
    ChatMessageDTO saveMessage(ChatMessageDTO dto);
    List<ChatMessageDTO> getMessage(String roomId, String currUsername);
    List<ChatRoomDTO> getUserChatRooms(String username);
}
