package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class CommunityNotFoundException extends CustomException {

    public CommunityNotFoundException() {
        super("Community not found", 404);
    }
}
