package com.example.forum.exception.s3;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class UploadFailedException extends CustomException {

    public UploadFailedException() {
        super("S3 upload failed", 400);
    }
}
