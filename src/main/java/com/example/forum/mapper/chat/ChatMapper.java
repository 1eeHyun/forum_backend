package com.example.forum.mapper.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.dto.chat.ChatRoomDTO;
import com.example.forum.mapper.profile.ProfileMapper;
import com.example.forum.model.chat.ChatMessage;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {


    public ChatMessageDTO toChatMessageDTO(ChatMessage msg) {
        User sender = msg.getSender();

        return ChatMessageDTO.builder()
                .id(msg.getId())
                .roomId(msg.getRoomId())
                .senderUsername(sender.getUsername())
                .senderProfile(ProfileMapper.toProfilePreviewDTO(sender))
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .build();
    }

    public ChatRoomDTO toChatRoomDTO(ChatRoom room, User currentUser, ChatMessage lastMessage) {
        User other = room.getUser1().equals(currentUser) ? room.getUser2() : room.getUser1();

        return ChatRoomDTO.builder()
                .roomId(room.getRoomId())
                .user(ProfileMapper.toProfilePreviewDTO(other))
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                .build();
    }
}
