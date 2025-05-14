package com.example.forum.exception.comment;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class CommentNotFoundException extends CustomException {
    public CommentNotFoundException() {
        super("Comment not found.", 400);
    }
}
