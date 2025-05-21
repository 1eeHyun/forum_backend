package com.example.forum.validator.notification;

import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.exception.notification.NotificationNotFoundException;
import com.example.forum.model.notification.Notification;
import com.example.forum.model.user.User;
import com.example.forum.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationValidator {

    private final NotificationRepository notificationRepository;

    public Notification validateExistingNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(NotificationNotFoundException::new);
    }

    public void validateSameUser(User expectedReceiver, User resultReceiver) {

        if (!expectedReceiver.equals(resultReceiver))
            throw new UnauthorizedException();
    }
}
