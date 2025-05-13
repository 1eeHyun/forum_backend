package com.example.forum.exception.post;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class PostNotAuthorException extends CustomException {

    public PostNotAuthorException() {
        super("Only the author can update this post.", 400);
    }
}
