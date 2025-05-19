package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.post.Post;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.comment.CommentRepository;
import com.example.forum.service.notification.NotificationService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.example.forum.model.notification.Notification.NotificationType.COMMENT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentServiceImpl Unit Tests")
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock private CommentValidator commentValidator;
    @Mock private CommentRepository commentRepository;
    @Mock private PostValidator postValidator;
    @Mock private AuthValidator userValidator;
    @Mock private NotificationService notificationService;

    private User mockUser;
    private Post mockPost;
    private Comment mockComment;

    @BeforeEach
    void setUp() {
        mockUser = User.builder().id(1L).username("tester").build();

        mockUser.setProfile(
                Profile.builder()
                        .nickname("tester-nickname")
                        .user(mockUser)
                        .build()
        );

        mockPost = Post.builder().id(100L).title("Sample Post").build();
        mockPost.setAuthor(mockUser);

        mockComment = Comment.builder()
                .id(10L)
                .content("Sample comment")
                .author(mockUser)
                .post(mockPost)
                .replies(new ArrayList<>())
                .build();

    }

    @Test
    void createComment_shouldReturnResponseDTO() {
        CommentRequestDTO dto = new CommentRequestDTO();
        dto.setPostId(100L);
        dto.setContent("New comment");

        when(postValidator.validatePost(100L)).thenReturn(mockPost);
        when(userValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponseDTO response = commentService.createComment("tester", dto);

        assertEquals("Sample comment", response.getContent());
        assertEquals("tester-nickname", response.getAuthor().getNickname());
    }

    @Test
    void createReply_shouldReturnResponseDTO() {
        CommentRequestDTO dto = new CommentRequestDTO();
        dto.setParentCommentId(10L);
        dto.setContent("Reply comment");

        when(userValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        when(commentValidator.validateCommentId(10L)).thenReturn(mockComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponseDTO response = commentService.createReply("tester", dto);

        assertEquals("Sample comment", response.getContent());
        assertEquals("tester-nickname", response.getAuthor().getNickname());
    }

    @Test
    void getCommentsByPostId_shouldReturnList() {
        when(commentRepository.findTopLevelCommentsWithReplies(100L)).thenReturn(List.of(mockComment));

        List<CommentResponseDTO> comments = commentService.getCommentsByPostId(100L);

        assertEquals(1, comments.size());
        assertEquals("Sample comment", comments.get(0).getContent());
    }

    @Test
    void deleteComment_shouldDeleteIfAuthorized() {
        when(commentValidator.validateCommentId(10L)).thenReturn(mockComment);

        doNothing().when(commentValidator)
                .validateCommentAuthor(eq("tester"), eq("tester"));

        commentService.deleteComment(10L, "tester");

        verify(commentRepository, times(1)).delete(mockComment);
    }

    @Test
    @DisplayName("Should throw exception when deleting someone else's comment")
    void deleteComment_shouldThrowExceptionIfUnauthorized() {
        Comment anotherUserComment = Comment.builder()
                .id(20L)
                .author(User.builder().username("someoneElse").build())
                .build();

        when(commentValidator.validateCommentId(20L)).thenReturn(anotherUserComment);

        doThrow(new RuntimeException("Unauthorized"))
                .when(commentValidator)
                .validateCommentAuthor(eq("someoneElse"), eq("tester"));

        assertThrows(RuntimeException.class,
                () -> commentService.deleteComment(20L, "tester"));
    }

    @Test
    @DisplayName("Should create a comment with a parent (nested comment)")
    void createComment_withParent_shouldReturnResponseDTO() {
        CommentRequestDTO dto = new CommentRequestDTO();
        dto.setPostId(100L);
        dto.setParentCommentId(10L);
        dto.setContent("Reply to post");

        when(postValidator.validatePost(100L)).thenReturn(mockPost);
        when(userValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        when(commentValidator.validateCommentId(10L)).thenReturn(mockComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(mockComment);

        CommentResponseDTO response = commentService.createComment("tester", dto);

        assertEquals("Sample comment", response.getContent());
        assertEquals("tester-nickname", response.getAuthor().getNickname());
    }


    @Test
    @DisplayName("Should send notification when comment is created")
    void createComment_shouldSendNotification() {
        CommentRequestDTO dto = new CommentRequestDTO();
        dto.setPostId(100L);
        dto.setContent("Test comment");

        when(postValidator.validatePost(100L)).thenReturn(mockPost);
        when(userValidator.validateUserByUsername("tester")).thenReturn(mockUser);

        // return a new Comment to simulate save behavior
        Comment newComment = Comment.builder()
                .id(99L)
                .post(mockPost)
                .author(mockUser)
                .content("Test comment")
                .build();

        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        commentService.createComment("tester", dto);

        // Capture arguments
        ArgumentCaptor<String> recipientCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> senderCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<com.example.forum.model.notification.Notification.NotificationType> typeCaptor = ArgumentCaptor.forClass(com.example.forum.model.notification.Notification.NotificationType.class);
        ArgumentCaptor<Long> postIdCaptor = ArgumentCaptor.forClass(Long.class);
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);

        verify(notificationService).sendNotification(
                recipientCaptor.capture(),
                senderCaptor.capture(),
                typeCaptor.capture(),
                postIdCaptor.capture(),
                commentCaptor.capture(),
                messageCaptor.capture()
        );

        assertEquals("tester", recipientCaptor.getValue());
        assertEquals("tester", senderCaptor.getValue());
        assertEquals(COMMENT, typeCaptor.getValue());
        assertEquals(100L, postIdCaptor.getValue());
        assertEquals("Test comment", commentCaptor.getValue().getContent());
        assertEquals("tester-nickname commented on your post.", messageCaptor.getValue());
    }

    @Test
    @DisplayName("Should return empty list when no comments found for post")
    void getCommentsByPostId_shouldReturnEmptyList() {
        when(commentRepository.findTopLevelCommentsWithReplies(200L)).thenReturn(List.of());

        List<CommentResponseDTO> result = commentService.getCommentsByPostId(200L);

        assertEquals(0, result.size());
    }
}
