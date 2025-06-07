package com.example.forum.mapper.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.dto.chat.ChatRoomDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.model.chat.ChatMessage;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import org.springframework.stereotype.Component;

@Component
public class ChatMapper {

    public ChatMessageDTO toChatMessageDTO(ChatMessage msg) {
        User sender = msg.getSender();
        Profile profile = sender.getProfile();

        return ChatMessageDTO.builder()
                .roomId(msg.getRoomId())
                .senderUsername(sender.getUsername())
                .senderProfile(toProfilePreviewDTO(sender))
                .content(msg.getContent())
                .sentAt(msg.getSentAt())
                .build();
    }

    public ProfilePreviewDTO toProfilePreviewDTO(User user) {
        Profile profile = user.getProfile();
        return ProfilePreviewDTO.builder()
                .username(user.getUsername())
                .nickname(profile.getNickname())
                .imageDto(ImageDTO.builder()
                        .imageUrl(profile.getImageUrl())
                        .imagePositionX(profile.getImagePositionX())
                        .imagePositionY(profile.getImagePositionY())
                        .build())
                .build();
    }

    public ChatRoomDTO toChatRoomDTO(ChatRoom room, User currentUser, ChatMessage lastMessage) {
        User other = room.getUser1().equals(currentUser) ? room.getUser2() : room.getUser1();

        return ChatRoomDTO.builder()
                .roomId(room.getRoomId())
                .user(toProfilePreviewDTO(other))
                .lastMessage(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                .build();
    }
}
