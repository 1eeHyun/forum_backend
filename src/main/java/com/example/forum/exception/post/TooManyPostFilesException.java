package com.example.forum.exception.post;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class TooManyPostFilesException extends CustomException {

    public TooManyPostFilesException() {
        super("You can upload up to 5 files per post.", 400);
    }
}
