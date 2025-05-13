package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super("User not found", 400);
    }
}
