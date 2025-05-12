package com.example.forum.exception;

import lombok.Getter;

@Getter
public class DuplicateEmailException extends CustomException {

    public DuplicateEmailException() {
        super("The email already exists.", 400);
    }
}
