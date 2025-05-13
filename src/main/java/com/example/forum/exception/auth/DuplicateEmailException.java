package com.example.forum.exception.auth;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class DuplicateEmailException extends CustomException {

    public DuplicateEmailException() {
        super("The email already exists.", 400);
    }
}
