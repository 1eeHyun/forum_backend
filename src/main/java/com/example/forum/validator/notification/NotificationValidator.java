package com.example.forum.validator.notification;

        import com.example.forum.exception.auth.NotAuthorizedException;
        import com.example.forum.model.notification.Notification;
        import com.example.forum.repository.notification.NotificationRepository;
        import lombok.RequiredArgsConstructor;
        import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
public class NotificationValidator {

    private final NotificationRepository notificationRepository;

    public Notification validateExistingNotification(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(NotAuthorizedException::new);
    }

    public void validateSameUser(String expectedReceiver, String resultReceiver) {

        if (!expectedReceiver.equals(resultReceiver))
            throw new NotAuthorizedException();
    }
}
