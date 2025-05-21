package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super("You are not authorized to do this action.", 401);
    }
}
