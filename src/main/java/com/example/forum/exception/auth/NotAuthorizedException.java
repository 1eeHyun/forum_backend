package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class NotAuthorizedException extends CustomException {

    public NotAuthorizedException() {
        super("Not authorized", 400);
    }
}
