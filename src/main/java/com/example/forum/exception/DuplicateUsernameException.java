package com.example.forum.exception;

public class DuplicateUsernameException extends CustomException {

    public DuplicateUsernameException() {
        super("The username already exists.", 400);
    }
}
