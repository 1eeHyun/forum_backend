package com.example.forum.exception.community;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class CategoryNotFoundException extends CustomException {

    public CategoryNotFoundException() {
        super("Category not found.", 400);
    }
}
