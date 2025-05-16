package com.example.forum.exception.notification;

import com.example.forum.exception.CustomException;
import lombok.Getter;

@Getter
public class NotificationNotFoundException extends CustomException {

    public NotificationNotFoundException() {
        super("Notification not found", 400);
    }
}
