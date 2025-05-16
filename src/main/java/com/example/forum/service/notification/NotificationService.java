package com.example.forum.service.notification;

import com.example.forum.model.notification.Notification;

import java.util.List;

public interface NotificationService {

    void sendNotification(String receiverUsername, String senderUsername,
                          Notification.NotificationType type,
                          Long targetId,
                          String message);

    List<Notification> getMyNotification(String username);

    void markAllAsRead(String username);
}
