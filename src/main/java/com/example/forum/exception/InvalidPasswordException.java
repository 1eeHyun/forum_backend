package com.example.forum.exception;

import lombok.Getter;

@Getter
public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super("Invalid password", 400);
    }
}
