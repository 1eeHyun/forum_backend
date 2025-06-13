package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class InvalidRuleForThisCommunity extends CustomException {

    public InvalidRuleForThisCommunity() {
        super("This rule does not belong to this community.", 400);
    }
}
