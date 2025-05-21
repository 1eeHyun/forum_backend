package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.exception.auth.UnauthorizedException;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.post.Post;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.comment.CommentRepository;
import com.example.forum.service.notification.NotificationService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static com.example.forum.model.notification.Notification.NotificationType.COMMENT;
import static com.example.forum.model.notification.Notification.NotificationType.REPLY;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
@DisplayName("CommentServiceImpl Unit Tests (Mockito-safe)")
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private AuthValidator userValidator;

    @Mock
    private NotificationService notificationService;

    @Test
    @DisplayName("Should create a top-level comment and send notification")
    void createComment_withValidData_shouldCreateCommentAndSendNotification() {
        // Arrange
        String username = "user1";
        Long postId = 1L;
        String content = "Test comment";
        CommentRequestDTO dto = new CommentRequestDTO();
        dto.setPostId(postId);
        dto.setContent(content);

        // Mock entities
        User user = mock(User.class);
        when(user.getUsername()).thenReturn(username);
        Profile profile = mock(Profile.class);
        when(profile.getNickname()).thenReturn("Nick");
        when(user.getProfile()).thenReturn(profile);

        Post post = mock(Post.class);
        User postAuthor = mock(User.class);
        when(post.getAuthor()).thenReturn(postAuthor);
        when(postAuthor.getUsername()).thenReturn("author1");

        Comment savedComment = mock(Comment.class);

        when(postValidator.validatePost(eq(postId))).thenReturn(post);
        when(userValidator.validateUserByUsername(eq(username))).thenReturn(user);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedComment);

        CommentResponseDTO responseDTO = CommentResponseDTO.builder().commentId(1L).content(content).build();
        try (MockedStatic<CommentMapper> mapper = mockStatic(CommentMapper.class)) {
            mapper.when(() -> CommentMapper.toDTO(any(Comment.class))).thenReturn(responseDTO);

            // Act
            CommentResponseDTO result = commentService.createComment(username, dto);

            // Assert
            assertNotNull(result);
            assertEquals(content, result.getContent());

            verify(notificationService).sendNotification(
                    eq("author1"), eq("user1"), eq(COMMENT), anyLong(), any(Comment.class), anyString());
            verify(commentRepository).save(any(Comment.class));
        }
    }


    @Test
    @DisplayName("Should create a reply and send reply notification")
    void createReply_withValidData_shouldCreateReplyAndSendNotification() {
        // Mock input DTO
        CommentRequestDTO dto = new CommentRequestDTO();
        dto.setParentCommentId(2L);
        dto.setContent("Reply comment");

        // Mock entities
        User user = mock(User.class);
        when(user.getUsername()).thenReturn("user2");

        Profile profile = mock(Profile.class);
        when(profile.getNickname()).thenReturn("ReplyNick");
        when(user.getProfile()).thenReturn(profile);

        Post post = mock(Post.class);

        User parentAuthor = mock(User.class);
        when(parentAuthor.getUsername()).thenReturn("parentAuthor");

        Comment parentComment = mock(Comment.class);
        when(parentComment.getAuthor()).thenReturn(parentAuthor);
        when(parentComment.getPost()).thenReturn(post);

        Comment savedReply = mock(Comment.class);

        // Mocks for repository and validator
        when(userValidator.validateUserByUsername("user2")).thenReturn(user);
        when(commentValidator.validateCommentId(2L)).thenReturn(parentComment);
        when(commentRepository.save(any(Comment.class))).thenReturn(savedReply);

        // Static mocking for DTO mapping
        CommentResponseDTO responseDTO = CommentResponseDTO.builder().commentId(2L).content("Reply comment").build();
        try (MockedStatic<CommentMapper> mapper = mockStatic(CommentMapper.class)) {
            mapper.when(() -> CommentMapper.toDTO(savedReply)).thenReturn(responseDTO);

            // Execute service
            CommentResponseDTO result = commentService.createReply("user2", dto);

            // Assertions and verifications
            assertNotNull(result);
            assertEquals("Reply comment", result.getContent());

            verify(notificationService).sendNotification(
                    eq("parentAuthor"), eq("user2"), eq(REPLY), anyLong(), eq(savedReply), anyString());
            verify(commentRepository).save(any(Comment.class));
        }
    }

    @Test
    @DisplayName("Should return comments by post id")
    void getCommentsByPostId_shouldReturnCommentDTOList() {
        // Mock data
        Long postId = 10L;
        Post post = mock(Post.class);
        Comment comment1 = mock(Comment.class);
        Comment comment2 = mock(Comment.class);
        List<Comment> comments = List.of(comment1, comment2);

        // Mocks for repository and validator
        when(postValidator.validatePost(postId)).thenReturn(post);
        when(commentRepository.findTopLevelCommentsWithReplies(postId)).thenReturn(comments);

        // Static mocking for DTO mapping
        CommentResponseDTO dto1 = CommentResponseDTO.builder().commentId(1L).content("C1").build();
        CommentResponseDTO dto2 = CommentResponseDTO.builder().commentId(2L).content("C2").build();

        try (MockedStatic<CommentMapper> mapper = mockStatic(CommentMapper.class)) {
            mapper.when(() -> CommentMapper.toDTO(comment1)).thenReturn(dto1);
            mapper.when(() -> CommentMapper.toDTO(comment2)).thenReturn(dto2);

            // Execute service
            List<CommentResponseDTO> result = commentService.getCommentsByPostId(postId);

            // Assertions
            assertNotNull(result);
            assertEquals(2, result.size());
            assertEquals("C1", result.get(0).getContent());
            assertEquals("C2", result.get(1).getContent());

            verify(postValidator).validatePost(postId);
            verify(commentRepository).findTopLevelCommentsWithReplies(postId);
        }
    }

    @Test
    @DisplayName("Should delete comment when user is author")
    void deleteComment_withAuthor_shouldDeleteComment() {
        // Mock data
        Long commentId = 100L;
        String username = "authorUser";
        User user = mock(User.class);
        Comment comment = mock(Comment.class);
        List<Comment> replies = new ArrayList<>(); // avoid NPE for getReplies().size()
        when(comment.getReplies()).thenReturn(replies);
        when(comment.getAuthor()).thenReturn(user);

        // Mocks for repository and validator
        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);

        // Execute service
        commentService.deleteComment(commentId, username);

        // Verify delete was called
        verify(commentRepository).delete(comment);
    }

    @Test
    @DisplayName("Should throw UnauthorizedException if deleteComment by non-author")
    void deleteComment_withNonAuthor_shouldThrowUnauthorized() {
        Long commentId = 101L;
        String username = "notAuthor";

        User author = mock(User.class); // Comment author
        User requestUser = mock(User.class); // Requester to delete the comment

        Comment comment = mock(Comment.class);
        when(comment.getAuthor()).thenReturn(author);

        when(userValidator.validateUserByUsername(eq(username))).thenReturn(requestUser);
        when(commentValidator.validateCommentId(eq(commentId))).thenReturn(comment);

        doThrow(new UnauthorizedException())
                .when(commentValidator).validateCommentAuthor(eq(author), eq(requestUser));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            commentService.deleteComment(commentId, username);
        });

        verify(commentRepository, never()).delete(any());
    }
}
