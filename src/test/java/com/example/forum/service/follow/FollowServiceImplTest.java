package com.example.forum.service.follow;

import com.example.forum.exception.auth.UserNotFoundException;
import com.example.forum.model.follow.Follow;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.follow.FollowRepository;
import com.example.forum.service.notification.NotificationHelper;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FollowServiceImplTest {

    @InjectMocks
    private FollowServiceImpl followService;

    @Mock private AuthValidator userValidator;

    @Mock private FollowRepository followRepository;

    @Mock private NotificationHelper notificationHelper;

    @Test
    @DisplayName("Should follow user when not already following")
    void followToggle_shouldFollow_whenNotAlreadyFollowing() {
        String currentUsername = "alice";
        String targetUsername = "bob";

        User currentUser = mock(User.class);
        User targetUser = mock(User.class);
        Profile currentProfile = mock(Profile.class);

        when(currentUser.getProfile()).thenReturn(currentProfile);
        when(currentProfile.getNickname()).thenReturn("Alice");

        when(userValidator.validateUserByUsername(currentUsername)).thenReturn(currentUser);
        when(userValidator.validateUserByUsername(targetUsername)).thenReturn(targetUser);
        when(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)).thenReturn(false);

        followService.followToggle(targetUsername, currentUsername);

        verify(followRepository).save(any(Follow.class));
    }


    @Test
    @DisplayName("Should unfollow user when already following")
    void followToggle_shouldUnfollow_whenAlreadyFollowing() {
        String currentUsername = "alice";
        String targetUsername = "bob";

        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(userValidator.validateUserByUsername(currentUsername)).thenReturn(currentUser);
        when(userValidator.validateUserByUsername(targetUsername)).thenReturn(targetUser);
        when(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)).thenReturn(true);

        followService.followToggle(targetUsername, currentUsername);

        verify(followRepository).deleteByFollowerAndFollowing(currentUser, targetUser);
    }

    @Test
    @DisplayName("Should return true when user is following target")
    void isFollowing_shouldReturnTrue_whenFollowing() {
        String currentUsername = "alice";
        String targetUsername = "bob";

        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(userValidator.validateUserByUsername(currentUsername)).thenReturn(currentUser);
        when(userValidator.validateUserByUsername(targetUsername)).thenReturn(targetUser);
        when(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)).thenReturn(true);

        boolean result = followService.isFollowing(targetUsername, currentUsername);

        assertTrue(result);
    }

    @Test
    @DisplayName("Should return false when user is not following target")
    void isFollowing_shouldReturnFalse_whenNotFollowing() {
        String currentUsername = "alice";
        String targetUsername = "bob";

        User currentUser = mock(User.class);
        User targetUser = mock(User.class);

        when(userValidator.validateUserByUsername(currentUsername)).thenReturn(currentUser);
        when(userValidator.validateUserByUsername(targetUsername)).thenReturn(targetUser);
        when(followRepository.existsByFollowerAndFollowing(currentUser, targetUser)).thenReturn(false);

        boolean result = followService.isFollowing(targetUsername, currentUsername);

        assertFalse(result);
    }

    @Test
    @DisplayName("Should throw exception if current user not found")
    void followToggle_shouldThrow_whenCurrentUserNotFound() {
        String currentUsername = "unknown";
        String targetUsername = "bob";

        when(userValidator.validateUserByUsername(currentUsername)).thenThrow(UserNotFoundException.class);

        assertThrows(RuntimeException.class, () -> followService.followToggle(targetUsername, currentUsername));
    }

    @Test
    @DisplayName("Should throw exception if target user not found")
    void followToggle_shouldThrow_whenTargetUserNotFound() {
        String currentUsername = "alice";
        String targetUsername = "unknown";

        when(userValidator.validateUserByUsername(targetUsername))
                .thenThrow(new UserNotFoundException());

        assertThrows(UserNotFoundException.class, () ->
                followService.followToggle(targetUsername, currentUsername));
    }
}
