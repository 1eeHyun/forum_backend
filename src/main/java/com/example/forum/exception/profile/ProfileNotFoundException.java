package com.example.forum.exception.profile;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class ProfileNotFoundException extends CustomException {

    public ProfileNotFoundException() {
        super("Profile not found", 400);
    }
}
