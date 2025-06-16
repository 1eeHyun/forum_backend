package com.example.forum.validator.chat;

import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.exception.chat.ChatRoomNotFoundException;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import com.example.forum.repository.chat.ChatRoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ChatValidatorTest {

    private ChatRoomRepository chatRoomRepository;
    private ChatValidator chatValidator;

    private User user1;
    private User user2;
    private ChatRoom chatRoom;

    @BeforeEach
    void setUp() {
        chatRoomRepository = mock(ChatRoomRepository.class);
        chatValidator = new ChatValidator(chatRoomRepository);

        user1 = User.builder().id(1L).username("user1").build();
        user2 = User.builder().id(2L).username("user2").build();

        chatRoom = ChatRoom.builder()
                .roomId("room123")
                .user1(user1)
                .user2(user2)
                .build();
    }

    @Test
    @DisplayName("validateUserRoom - passes if user is a participant")
    void validateUserRoom_success() {
        when(chatRoomRepository.findByRoomId("room123")).thenReturn(Optional.of(chatRoom));

        assertDoesNotThrow(() -> chatValidator.validateUserRoom("room123", user1));
        assertDoesNotThrow(() -> chatValidator.validateUserRoom("room123", user2));
    }

    @Test
    @DisplayName("validateUserRoom - throws if room does not exist")
    void validateUserRoom_roomNotFound() {
        when(chatRoomRepository.findByRoomId("room123")).thenReturn(Optional.empty());

        assertThrows(ChatRoomNotFoundException.class,
                () -> chatValidator.validateUserRoom("room123", user1));
    }

    @Test
    @DisplayName("validateUserRoom - throws if user is not in the room")
    void validateUserRoom_userNotInRoom() {
        User stranger = User.builder().id(3L).username("stranger").build();
        when(chatRoomRepository.findByRoomId("room123")).thenReturn(Optional.of(chatRoom));

        assertThrows(UnauthorizedException.class,
                () -> chatValidator.validateUserRoom("room123", stranger));
    }
}
