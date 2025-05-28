package com.example.forum.service.notification;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.notification.Notification.NotificationType;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import static org.mockito.Mockito.*;

class NotificationHelperTest {

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private NotificationHelper notificationHelper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should not send notification if receiver and sender are the same")
    void sendIfNotSelf_shouldNotSend_whenReceiverIsSender() {
        // Arrange
        User user = mock(User.class);

        // Act
        notificationHelper.sendIfNotSelf(user, user, null, null, NotificationType.FOLLOW, "test message");

        // Assert
        // NotificationService should not be called
        verify(notificationService, never()).sendNotification(any(), any(), any(), any(), any(), anyString());
    }

    @Test
    @DisplayName("Should send notification if receiver and sender are different")
    void sendIfNotSelf_shouldSend_whenReceiverIsDifferentFromSender() {
        // Arrange
        User receiver = mock(User.class);
        User sender = mock(User.class);
        Post post = mock(Post.class);
        Comment comment = mock(Comment.class);

        when(receiver.getUsername()).thenReturn("receiver");
        when(sender.getUsername()).thenReturn("sender");
        when(post.getId()).thenReturn(100L);

        // Act
        notificationHelper.sendIfNotSelf(receiver, sender, post, comment, NotificationType.POST_LIKE, "You got a like");

        // Assert
        verify(notificationService).sendNotification(
                eq("receiver"),
                eq("sender"),
                eq(NotificationType.POST_LIKE),
                eq(100L),
                eq(comment),
                eq("You got a like")
        );
    }
}
