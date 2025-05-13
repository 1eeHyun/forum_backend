package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class CommunityExistsNameException extends CustomException {

    public CommunityExistsNameException() {
        super("Community name already exists.", 400);
    }
}
