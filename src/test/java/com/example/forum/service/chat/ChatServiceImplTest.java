package com.example.forum.service.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.model.chat.ChatMessage;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import com.example.forum.repository.chat.ChatMessageRepository;
import com.example.forum.repository.chat.ChatRoomRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.chat.ChatValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceImplTest {

    @InjectMocks
    private ChatServiceImpl chatService;

    @Mock
    private ChatRoomRepository chatRoomRepository;
    @Mock private ChatMessageRepository chatMessageRepository;
    @Mock private AuthValidator userValidator;
    @Mock private ChatValidator chatValidator;

    private final Long user1Id = 1L;
    private final Long user2Id = 2L;
    private final String roomId = "1_2";

    @Test
    @DisplayName("Should return existing roomId if chat room already exists")
    void testGetOrCreateRoomId_existing() {
        ChatRoom room = new ChatRoom();
        room.setRoomId(roomId);
        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(Optional.of(room));

        String result = chatService.getOrCreateRoomId(user1Id, user2Id);

        assertEquals(roomId, result);
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create and return new roomId if chat room does not exist")
    void testGetOrCreateRoomId_createNew() {
        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(Optional.empty());

        String result = chatService.getOrCreateRoomId(user1Id, user2Id);

        assertEquals(roomId, result);
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }

    @Test
    @DisplayName("Should save chat message to repository")
    void testSaveMessage() {
        ChatMessageDTO dto = ChatMessageDTO.builder()
                .roomId(roomId)
                .senderId(user1Id)
                .content("Hello")
                .sentAt(LocalDateTime.now())
                .build();

        chatService.saveMessage(dto);

        verify(chatMessageRepository).save(any(ChatMessage.class));
    }

    @Test
    @DisplayName("Should return messages as DTOs for a valid user and room")
    void testGetMessage() throws Exception {
        User user = User.builder()
                .username("user1")
                .password("dummy")
                .email("user1@example.com")
                .build();

        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user, user1Id);

        when(userValidator.validateUserByUsername("user1")).thenReturn(user);
        doNothing().when(chatValidator).validateUserRoom(roomId, user1Id);

        ChatMessage message = ChatMessage.builder()
                .roomId(roomId)
                .senderId(user1Id)
                .content("Hi")
                .sentAt(LocalDateTime.now())
                .build();

        when(chatMessageRepository.findByRoomIdOrderBySentAtAsc(roomId))
                .thenReturn(List.of(message));

        List<ChatMessageDTO> result = chatService.getMessage(roomId, "user1");

        assertEquals(1, result.size());
        assertEquals("Hi", result.get(0).getContent());
    }
}
