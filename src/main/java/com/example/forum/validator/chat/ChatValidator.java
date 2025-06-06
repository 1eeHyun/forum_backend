package com.example.forum.validator.chat;

import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.exception.chat.ChatRoomNotFoundException;
import com.example.forum.model.chat.ChatRoom;
import com.example.forum.repository.chat.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatValidator {

    private final ChatRoomRepository chatRoomRepository;

    public void validateUserRoom(String roomId, Long userId) {
        ChatRoom room = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(ChatRoomNotFoundException::new);

        if (!room.getUser1Id().equals(userId) && !room.getUser2Id().equals(userId))
            throw new UnauthorizedException();
    }
}
