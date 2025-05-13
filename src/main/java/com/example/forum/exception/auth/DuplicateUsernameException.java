package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;

public class DuplicateUsernameException extends CustomException {

    public DuplicateUsernameException() {
        super("The username already exists.", 400);
    }
}
