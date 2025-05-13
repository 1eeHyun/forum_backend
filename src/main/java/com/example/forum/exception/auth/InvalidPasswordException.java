package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super("Invalid password", 400);
    }
}
