package com.example.forum.dto.notification;

import com.example.forum.dto.util.AuthorDTO;
import com.example.forum.model.notification.Notification.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponseDTO {

    private Long notificationId;
    private AuthorDTO sender;
    private NotificationType type;
    private Long targetId;
    private Long commentId;
    private String message;
    private Boolean isRead;
    private LocalDateTime createdAt;
}
