package com.example.forum.service.notification;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.notification.Notification.NotificationType;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NotificationHelper {

    private final NotificationService notificationService;
//    private final EmailService emailService;

    public void sendIfNotSelf(User receiver, User sender, Post post, Comment comment, NotificationType type, String message) {

        if (receiver.equals(sender))
            return;

        Long postId = (post != null) ? post.getId() : null;

        notificationService.sendNotification(
                receiver.getUsername(),
                sender.getUsername(),
                type,
                postId,
                comment,
                message
        );

        // TODO: future implementation, sending a notification email
//            if (receiver.isEmailNotificationEnabled()) {
//                emailService.sendNotificationEmail(receiver, message);
//            }

    }
}


