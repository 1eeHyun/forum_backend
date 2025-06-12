package com.example.forum.mapper.notification;

import com.example.forum.dto.notification.NotificationResponseDTO;
import com.example.forum.mapper.user.UserMapper;
import com.example.forum.model.notification.Notification;

public class NotificationMapper {

    public static NotificationResponseDTO toDto(Notification notification) {

        return NotificationResponseDTO.builder()
                .notificationId(notification.getId())
                .type(notification.getType())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .sender(UserMapper.toDtoWithEmail(notification.getSender()))
                .targetId(notification.getTargetId())
                .commentId(notification.getComment() != null ? notification.getComment().getId() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
