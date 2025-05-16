package com.example.forum.mapper.notification;

import com.example.forum.dto.notification.NotificationResponseDTO;
import com.example.forum.mapper.auth.AuthorMapper;
import com.example.forum.model.notification.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO toDto(Notification notification) {
        return NotificationResponseDTO.builder()
                .notificationId(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .sender(AuthorMapper.toDto(notification.getSender()))
                .targetId(notification.getTargetId())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
