package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class InvalidCategoryForThisCommunity extends CustomException {

    public InvalidCategoryForThisCommunity() {
        super("This category does not belong to this community.", 400);
    }
}
