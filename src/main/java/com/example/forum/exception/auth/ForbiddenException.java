package com.example.forum.exception.auth;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {

    public ForbiddenException() {
        super("You are not authorized to do this action.");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
