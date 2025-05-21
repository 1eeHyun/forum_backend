package com.example.forum.common;

public enum SortOrder {
    TOP_LIKED,
    OLDEST,
    NEWEST;

    public static SortOrder from(String value) {
        return switch (value.toLowerCase()) {
            case "newest" -> NEWEST;
            case "oldest" -> OLDEST;
            case "top", "top_liked", "topliked" -> TOP_LIKED;
            default -> throw new IllegalArgumentException("Invalid sort option: " + value);
        };
    }
}
