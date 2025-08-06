package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;

public class ForbiddenException extends CustomException {

    public ForbiddenException() {
        super("You are not authorized to do this action.", 403);
    }

    public ForbiddenException(String message) {
        super(message, 403);
    }
}
