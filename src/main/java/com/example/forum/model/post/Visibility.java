package com.example.forum.model.post;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Visibility {
    PUBLIC,
    COMMUNITY,
    PRIVATE;

    @JsonCreator
    public static Visibility from(String value) {
        return switch (value.toLowerCase()) {
            case "public", "PUBLIC", "Public" -> PUBLIC;
            case "community", "COMMUNITY", "Community" -> COMMUNITY;
            case "private", "PRIVATE", "Private" -> PRIVATE;
            default -> throw new IllegalArgumentException("Invalid visibility: " + value);
        };
    }
}
