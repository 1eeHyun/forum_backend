package com.example.forum.service.notification;

import com.example.forum.dto.notification.LinkResponseDTO;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.notification.Notification;
import com.example.forum.model.notification.Notification.NotificationType;
import com.example.forum.model.user.User;
import com.example.forum.repository.notification.NotificationRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.notification.NotificationValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceImplTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @Mock private AuthValidator userValidator;
    @Mock private NotificationRepository notificationRepository;
    @Mock private NotificationValidator notificationValidator;

    private User sender;
    private User receiver;
    private Comment comment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        sender = User.builder().id(1L).username("sender").build();
        receiver = User.builder().id(2L).username("receiver").build();
        comment = Comment.builder().id(101L).content("Sample").build();
    }

    @Test
    @DisplayName("Should send notification if sender and receiver are different")
    void sendNotification_shouldSaveNotification() {
        when(userValidator.validateUserByUsername("sender")).thenReturn(sender);
        when(userValidator.validateUserByUsername("receiver")).thenReturn(receiver);

        notificationService.sendNotification("receiver", "sender", NotificationType.COMMENT, 10L, comment, "msg");

        verify(notificationRepository).save(argThat(notification ->
                notification.getReceiver().equals(receiver) &&
                        notification.getSender().equals(sender) &&
                        notification.getType() == NotificationType.COMMENT &&
                        notification.getTargetId().equals(10L) &&
                        notification.getComment().equals(comment) &&
                        notification.getMessage().equals("msg")
        ));
    }

    @Test
    @DisplayName("Should NOT send notification if sender and receiver are same")
    void sendNotification_shouldNotSendToSelf() {
        when(userValidator.validateUserByUsername("sender")).thenReturn(sender);

        notificationService.sendNotification("sender", "sender", NotificationType.COMMENT, 10L, comment, "msg");

        verify(notificationRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should return notifications for given user")
    void getMyNotification_shouldReturnNotifications() {
        when(userValidator.validateUserByUsername("receiver")).thenReturn(receiver);

        List<Notification> fakeList = List.of(Notification.builder().receiver(receiver).build());
        when(notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver)).thenReturn(fakeList);

        List<Notification> result = notificationService.getMyNotification("receiver");

        assertEquals(1, result.size());
    }

    @Test
    @DisplayName("Should mark all notifications as read")
    void markAllAsRead_shouldUpdateNotifications() {
        Notification n1 = Notification.builder().receiver(receiver).isRead(false).build();
        Notification n2 = Notification.builder().receiver(receiver).isRead(false).build();

        when(userValidator.validateUserByUsername("receiver")).thenReturn(receiver);
        when(notificationRepository.findByReceiverOrderByCreatedAtDesc(receiver)).thenReturn(List.of(n1, n2));

        notificationService.markAllAsRead("receiver");

        assertTrue(n1.isRead());
        assertTrue(n2.isRead());
        verify(notificationRepository).saveAll(List.of(n1, n2));
    }

    @Test
    @DisplayName("Should resolve notification and return link")
    void resolveAndMarkAsRead_shouldReturnLinkDTO() {
        Notification n = Notification.builder()
                .id(1L)
                .receiver(receiver)
                .sender(sender)
                .type(NotificationType.COMMENT)
                .targetId(99L)
                .comment(comment)
                .isRead(false)
                .build();

        when(notificationValidator.validateExistingNotification(1L)).thenReturn(n);
        doNothing().when(notificationValidator).validateSameUser("receiver", "receiver");
        when(notificationRepository.save(any())).thenReturn(n);

        LinkResponseDTO result = notificationService.resolveAndMarkAsRead(1L, "receiver");

        assertTrue(result.getLink().contains("postId=99"));
        assertTrue(result.getLink().contains("commentId=101"));
    }
}
