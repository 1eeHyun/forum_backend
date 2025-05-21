package com.example.forum.service.notification;


import com.example.forum.dto.notification.LinkResponseDTO;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.notification.Notification;
import com.example.forum.model.notification.Notification.NotificationType;
import com.example.forum.model.user.User;
import com.example.forum.repository.notification.NotificationRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.notification.NotificationValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AuthValidator userValidator;
    private final NotificationRepository notificationRepository;
    private final NotificationValidator notificationValidator;

    @Override
    public void sendNotification(String receiverUsername, String senderUsername, NotificationType type, Long targetId, Comment comment, String message) {
        User receiver = userValidator.validateUserByUsername(receiverUsername);
        User sender = userValidator.validateUserByUsername(senderUsername);

        if (receiver == sender) return;

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .targetId(targetId)
                .comment(comment)
                .message(message)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getMyNotification(String username) {

        User user = userValidator.validateUserByUsername(username);

        return notificationRepository.findByReceiverOrderByCreatedAtDesc(user);
    }

    @Override
    public void markAllAsRead(String username) {

        User user = userValidator.validateUserByUsername(username);

        List<Notification> list = notificationRepository.findByReceiverOrderByCreatedAtDesc(user);
        list.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(list);
    }

    @Override
    public LinkResponseDTO resolveAndMarkAsRead(Long notificationId, String username) {

        Notification notification = notificationValidator.validateExistingNotification(notificationId);

        User receiver = userValidator.validateUserByUsername(notification.getReceiver().getUsername());
        User user = userValidator.validateUserByUsername(username);

        notificationValidator.validateSameUser(receiver, user);

        notification.setRead(true);
        notificationRepository.save(notification);

        Long postId = notification.getTargetId();
        Long commentId = notification.getComment() != null ? notification.getComment().getId() : null;

        String link = getLink(notification, postId, commentId);

        return new LinkResponseDTO(link);
    }

    private String getLink(Notification notification, Long postId, Long commentId) {

        switch (notification.getType()) {
            case COMMENT:
            case REPLY:
            case POST_LIKE:
            case COMMENT_LIKE:

                return commentId != null ? "/?postId=" + postId + "&commentId=" + commentId
                        : "/?postId=" + postId;
            case FOLLOW:

                return "/profile/" + notification.getSender().getUsername();
            case JOINED_COMMUNITY:

                return "/community/" + postId;

            default:
                return "/";

        }

    }
}
