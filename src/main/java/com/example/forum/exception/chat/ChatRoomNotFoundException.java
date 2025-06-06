package com.example.forum.exception.chat;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class ChatRoomNotFoundException extends CustomException {

    public ChatRoomNotFoundException() {
        super("Chat room  not found.", 404);
    }
}
