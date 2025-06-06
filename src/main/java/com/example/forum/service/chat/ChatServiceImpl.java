package com.example.forum.service.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.model.chat.ChatMessage;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import com.example.forum.repository.chat.ChatMessageRepository;
import com.example.forum.repository.chat.ChatRoomRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.chat.ChatValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // Repositories
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // Validators
    private final AuthValidator userValidator;
    private final ChatValidator chatValidator;

    @Override
    public String getOrCreateRoomId(Long user1Id, Long user2Id) {

        Long min = Math.min(user1Id, user2Id);
        Long max = Math.max(user1Id, user2Id);
        String roomId = min + "_" + max;

        // If chat exists with the two users, get the chat room
        // otherwise, create a new chat room and return the room id
        return chatRoomRepository.findByRoomId(roomId)
                .map(ChatRoom::getRoomId)
                .orElseGet(() -> {

                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);
                    newRoom.setUser1Id(min);
                    newRoom.setUser2Id(max);

                    chatRoomRepository.save(newRoom);

                    return roomId;
                });
    }

    @Override
    public void saveMessage(ChatMessageDTO dto) {

        ChatMessage message = ChatMessage.builder()
                .roomId(dto.getRoomId())
                .senderId(dto.getSenderId())
                .content(dto.getContent())
                .sentAt(dto.getSentAt() != null ? dto.getSentAt() : LocalDateTime.now())
                .build();

        chatMessageRepository.save(message);
    }

    @Override
    public List<ChatMessageDTO> getMessage(String roomId, String currUsername) {

        User user = userValidator.validateUserByUsername(currUsername);
        chatValidator.validateUserRoom(roomId, user.getId());

        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(msg -> ChatMessageDTO.builder()
                        .roomId(msg.getRoomId())
                        .senderId(msg.getSenderId())
                        .content(msg.getContent())
                        .sentAt(msg.getSentAt())
                        .build())
                .toList();
    }
}
