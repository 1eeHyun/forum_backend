package com.example.forum.service.notification;


import com.example.forum.model.notification.Notification;
import com.example.forum.model.user.User;
import com.example.forum.repository.notification.NotificationRepository;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final AuthValidator userValidator;
    private final NotificationRepository notificationRepository;

    @Override
    public void sendNotification(String receiverUsername, String senderUsername, Notification.NotificationType type, Long targetId, String message) {
        User receiver = userValidator.validateUserByUsername(receiverUsername);
        User sender = userValidator.validateUserByUsername(senderUsername);

        Notification notification = Notification.builder()
                .receiver(receiver)
                .sender(sender)
                .type(type)
                .targetId(targetId)
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
}
