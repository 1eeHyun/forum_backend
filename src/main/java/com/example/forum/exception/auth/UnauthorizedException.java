package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super("You need to log in first.", 401);
    }
}
