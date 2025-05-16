package com.example.forum.factory;

import com.example.forum.model.notification.Notification;
import com.example.forum.model.notification.Notification.NotificationType;
import com.example.forum.model.user.User;

import java.time.LocalDateTime;

public class NotificationFactory {

    public static Notification create(User sender, User receiver,
                                      NotificationType type,
                                      Long targetId,
                                      String message) {

        return Notification.builder()
                .sender(sender)
                .receiver(receiver)
                .type(type)
                .targetId(targetId)
                .message(message)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
