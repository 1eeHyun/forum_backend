package com.example.forum.exception.post;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class TagRequiredException extends CustomException {

    public TagRequiredException() {
        super("Public post must contain at least one tag.", 400);
    }
}
