package com.example.forum.service.follow;

import com.example.forum.model.follow.Follow;
import com.example.forum.model.user.User;
import com.example.forum.repository.follow.FollowRepository;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService{

    private final AuthValidator userValidator;
    private final FollowRepository followRepository;

    @Override
    public void follow(String targetUsername, String currentUsername) {

        User target = userValidator.validateUserByUsername(targetUsername);
        User user = userValidator.validateUserByUsername(currentUsername);

        if (followRepository.existsByFollowerAndFollowing(target, user))
            return;

        Follow follow = new Follow();
        follow.setFollower(target);
        follow.setFollowing(user);
        followRepository.save(follow);
    }

    @Override
    public void unfollow(String targetUsername, String currentUsername) {

        User target = userValidator.validateUserByUsername(targetUsername);
        User user = userValidator.validateUserByUsername(currentUsername);

        followRepository.deleteByFollowerAndFollowing(target, user);
    }

    @Override
    public boolean isFollowing(String targetUsername, String currentUsername) {

        User target = userValidator.validateUserByUsername(targetUsername);
        User user = userValidator.validateUserByUsername(currentUsername);

        return followRepository.existsByFollowerAndFollowing(target, user);
    }
}
