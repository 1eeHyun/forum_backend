package com.example.forum.exception.post;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class PostNotFoundException extends CustomException {

    public PostNotFoundException() {
        super("Post not found", 400);
    }
}
