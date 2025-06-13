package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class RuleNotFoundException  extends CustomException {

    public RuleNotFoundException() {
        super("Rule not found.", 404);
    }
}
