package com.example.forum.service.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.dto.chat.ChatRoomDTO;
import com.example.forum.mapper.chat.ChatMapper;
import com.example.forum.model.chat.ChatMessage;
import com.example.forum.model.chat.ChatReadStatus;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import com.example.forum.repository.chat.ChatMessageRepository;
import com.example.forum.repository.chat.ChatReadStatusRepository;
import com.example.forum.repository.chat.ChatRoomRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.chat.ChatValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    // Repositories
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;

    // Validators
    private final AuthValidator userValidator;
    private final ChatValidator chatValidator;

    // Mapper
    private final ChatMapper chatMapper;

    @Override
    public String getOrCreateRoomId(String user1Username, String user2Username) {

        User user1 = userValidator.validateUserByUsername(user1Username);
        User user2 = userValidator.validateUserByUsername(user2Username);

        Long min = Math.min(user1.getId(), user2.getId());
        Long max = Math.max(user1.getId(), user2.getId());
        String roomId = min + "_" + max;

        return chatRoomRepository.findByRoomId(roomId)
                .map(ChatRoom::getRoomId)
                .orElseGet(() -> {
                    ChatRoom newRoom = new ChatRoom();
                    newRoom.setRoomId(roomId);

                    if (user1.getId().equals(min)) {
                        newRoom.setUser1(user1);
                        newRoom.setUser2(user2);
                    } else {
                        newRoom.setUser1(user2);
                        newRoom.setUser2(user1);
                    }

                    chatRoomRepository.save(newRoom);
                    return roomId;
                });
    }

    @Override

    public ChatMessageDTO saveMessage(ChatMessageDTO dto) {

        User sender = userValidator.validateUserByUsername(dto.getSenderUsername());

        LocalDateTime sentAt = LocalDateTime.now();

        ChatMessage message = ChatMessage.builder()
                .roomId(dto.getRoomId())
                .sender(sender)
                .content(dto.getContent())
                .sentAt(sentAt)
                .build();

        ChatMessage saved = chatMessageRepository.save(message);
        return chatMapper.toChatMessageDTO(saved);
    }

    @Override
    public List<ChatMessageDTO> getMessage(String roomId, String currUsername) {

        User currUser = userValidator.validateUserByUsername(currUsername);
        chatValidator.validateUserRoom(roomId, currUser);

        return chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId)
                .stream()
                .map(chatMapper::toChatMessageDTO)
                .toList();
    }

    @Override
    public List<ChatRoomDTO> getUserChatRooms(String username) {
        User me = userValidator.validateUserByUsername(username);

        return chatRoomRepository.findAllByUser1OrUser2(me, me)
                .stream()
                .map(room -> {
                    ChatMessage lastMessage = chatMessageRepository
                            .findTopByRoomIdOrderBySentAtDesc(room.getRoomId())
                            .orElse(null);
                    return chatMapper.toChatRoomDTO(room, me, lastMessage);
                })
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(String roomId, String username, Long lastReadMessageId) {

        User user = userValidator.validateUserByUsername(username);

        ChatReadStatus status = chatReadStatusRepository.findByRoomIdAndUser(roomId, user)
                .orElse(ChatReadStatus.builder()
                        .roomId(roomId)
                        .user(user)
                        .build());

        // If a user reads more messages, renewal
        if (status.getLastReadMessageId() == null || lastReadMessageId > status.getLastReadMessageId()) {
            status.setLastReadMessageId(lastReadMessageId);
            status.setUpdatedAt(LocalDateTime.now());
            chatReadStatusRepository.save(status);
        }
    }
}
