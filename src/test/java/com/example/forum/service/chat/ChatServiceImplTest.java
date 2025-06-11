package com.example.forum.service.chat;

import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import com.example.forum.repository.chat.ChatRoomRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.chat.ChatValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {

    @Mock
    private ChatRoomRepository chatRoomRepository;

    @Mock
    private AuthValidator userValidator;

    @Mock
    private ChatValidator chatValidator;

    @InjectMocks
    private ChatServiceImpl chatService;

    private User user1;
    private User user2;
    private final String user1Username = "user1";
    private final String user2Username = "user2";
    private final Long user1Id = 1L;
    private final Long user2Id = 2L;
    private final String roomId = "1_2";

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);

        user1 = User.builder().username(user1Username).email("u1@example.com").password("pw1").build();
        user2 = User.builder().username(user2Username).email("u2@example.com").password("pw2").build();

        // Set private field 'id' via reflection
        Field idField = User.class.getDeclaredField("id");
        idField.setAccessible(true);
        idField.set(user1, user1Id);
        idField.set(user2, user2Id);
    }

    @Test
    @DisplayName("Should return existing roomId if chat room already exists")
    void testGetOrCreateRoomId_existing() {
        // given
        ChatRoom existingRoom = new ChatRoom();
        existingRoom.setRoomId(roomId);

        when(userValidator.validateUserByUsername(user1Username)).thenReturn(user1);
        when(userValidator.validateUserByUsername(user2Username)).thenReturn(user2);
        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(Optional.of(existingRoom));

        // when
        String result = chatService.getOrCreateRoomId(user1Username, user2Username);

        // then
        assertThat(result).isEqualTo(roomId);
        verify(chatRoomRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should create new chat room if it does not exist")
    void testGetOrCreateRoomId_createNew() {
        // given
        when(userValidator.validateUserByUsername(user1Username)).thenReturn(user1);
        when(userValidator.validateUserByUsername(user2Username)).thenReturn(user2);
        when(chatRoomRepository.findByRoomId(roomId)).thenReturn(Optional.empty());

        // when
        String result = chatService.getOrCreateRoomId(user1Username, user2Username);

        // then
        assertThat(result).isEqualTo(roomId);
        verify(chatRoomRepository).save(any(ChatRoom.class));
    }
}
