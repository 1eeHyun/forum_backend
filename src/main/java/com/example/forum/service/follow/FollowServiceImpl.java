package com.example.forum.service.follow;

import com.example.forum.model.follow.Follow;
import com.example.forum.model.user.User;
import com.example.forum.repository.follow.FollowRepository;
import com.example.forum.service.notification.NotificationHelper;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.forum.model.notification.Notification.NotificationType.FOLLOW;
import static com.example.forum.service.notification.NotificationMessageBuilder.buildFollowNotification;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService{

    // Validators
    private final AuthValidator userValidator;

    // Repositories
    private final FollowRepository followRepository;

    // Services
    private final NotificationHelper notificationHelper;

    @Override
    @Transactional
    public void followToggle(String targetUsername, String currentUsername) {

        User target = userValidator.validateUserByUsername(targetUsername);
        User user = userValidator.validateUserByUsername(currentUsername);

        if (followRepository.existsByFollowerAndFollowing(user, target)) {
            followRepository.deleteByFollowerAndFollowing(user, target);
            return;
        }

        Follow follow = new Follow();
        follow.setFollower(user);
        follow.setFollowing(target);
        followRepository.save(follow);

        // Follow notification
        String message = buildFollowNotification(user.getProfile().getNickname());

        notificationHelper.sendIfNotSelf(
                target,                      // receiver
                user,                        // sender
                null,                        // post
                null,                        // comment
                FOLLOW,
                message                      // message
        );
    }

    @Override
    public boolean isFollowing(String targetUsername, String currentUsername) {

        User target = userValidator.validateUserByUsername(targetUsername);
        User user = userValidator.validateUserByUsername(currentUsername);

        return followRepository.existsByFollowerAndFollowing(user, target);
    }
}
