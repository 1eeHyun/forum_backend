package com.example.forum.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class CommonResponse<T> {

    private int status;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(200, "Request success", data, LocalDateTime.now());
    }

    public static CommonResponse<Void> success() {
        return new CommonResponse<>(200, "Request success", null, LocalDateTime.now());
    }

    public static <T> CommonResponse<T> fail(int status, String message) {
        return new CommonResponse<>(status, message, null, LocalDateTime.now());
    }
}
