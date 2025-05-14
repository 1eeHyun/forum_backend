package com.example.forum.exception.comment;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class CommentNotAuthorException extends CustomException {

    public CommentNotAuthorException() {
        super("Unauthorized", 400);
    }
}
