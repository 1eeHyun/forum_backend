package com.example.forum.exception.post;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class TooManyPostImagesException extends CustomException {

    public TooManyPostImagesException() {
        super("You can upload up to 5 images per post.", 400);
    }
}
