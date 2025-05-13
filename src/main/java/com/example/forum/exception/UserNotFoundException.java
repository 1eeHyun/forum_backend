package com.example.forum.exception;

import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super("User not found", 400);
    }
}
