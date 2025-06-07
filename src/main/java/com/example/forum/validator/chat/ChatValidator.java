package com.example.forum.validator.chat;

import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.exception.chat.ChatRoomNotFoundException;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.model.user.User;
import com.example.forum.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatValidator {

    private final ChatRoomRepository chatRoomRepository;

    public void validateUserRoom(String roomId, User user) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        if (!room.getUser1().equals(user) && !room.getUser2().equals(user))
            throw new UnauthorizedException();
    }
}
