package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class UserNotMemberException extends CustomException {

    public UserNotMemberException() {
        super("User is not a member of this community", 400);
    }
}
