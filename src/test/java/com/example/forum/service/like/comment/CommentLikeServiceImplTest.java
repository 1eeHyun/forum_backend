package com.example.forum.service.like.comment;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.like.CommentDislike;
import com.example.forum.model.like.CommentLike;
import com.example.forum.model.user.User;
import com.example.forum.repository.like.CommentDislikeRepository;
import com.example.forum.repository.like.CommentLikeRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceImplTest {

    @InjectMocks
    private CommentLikeServiceImpl commentLikeService;

    @Mock
    private CommentLikeRepository likeRepo;

    @Mock
    private CommentDislikeRepository dislikeRepo;

    @Mock
    private CommentValidator commentValidator;

    @Mock
    private AuthValidator userValidator;

    @Test
    @DisplayName("Should toggle like - add new like")
    void toggleLike_addNewLike() {
        Long commentId = 1L;
        String username = "john";
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(likeRepo.findByCommentAndUser(comment, user)).thenReturn(Optional.empty());

        commentLikeService.toggleLike(commentId, username);

        verify(dislikeRepo).deleteByCommentAndUser(comment, user);
        verify(likeRepo).save(any(CommentLike.class));
    }

    @Test
    @DisplayName("Should toggle like - remove existing like")
    void toggleLike_removeExistingLike() {
        Long commentId = 1L;
        String username = "john";
        Comment comment = mock(Comment.class);
        User user = mock(User.class);
        CommentLike like = mock(CommentLike.class);

        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(likeRepo.findByCommentAndUser(comment, user)).thenReturn(Optional.of(like));

        commentLikeService.toggleLike(commentId, username);

        verify(dislikeRepo).deleteByCommentAndUser(comment, user);
        verify(likeRepo).delete(like);
    }

    @Test
    @DisplayName("Should toggle dislike - add new dislike")
    void toggleDislike_addNewDislike() {
        Long commentId = 1L;
        String username = "john";
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(dislikeRepo.findByCommentAndUser(comment, user)).thenReturn(Optional.empty());

        commentLikeService.toggleDislike(commentId, username);

        verify(likeRepo).deleteByCommentAndUser(comment, user);
        verify(dislikeRepo).save(any(CommentDislike.class));
    }

    @Test
    @DisplayName("Should toggle dislike - remove existing dislike")
    void toggleDislike_removeExistingDislike() {
        Long commentId = 1L;
        String username = "john";
        Comment comment = mock(Comment.class);
        User user = mock(User.class);
        CommentDislike dislike = mock(CommentDislike.class);

        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(dislikeRepo.findByCommentAndUser(comment, user)).thenReturn(Optional.of(dislike));

        commentLikeService.toggleDislike(commentId, username);

        verify(likeRepo).deleteByCommentAndUser(comment, user);
        verify(dislikeRepo).delete(dislike);
    }

    @Test
    @DisplayName("Should return count of likes")
    void countLikes_shouldReturnCount() {
        Long commentId = 1L;
        Comment comment = mock(Comment.class);
        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(likeRepo.countByComment(comment)).thenReturn(5L);

        long count = commentLikeService.countLikes(commentId);

        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Should return true if user liked")
    void hasUserLiked_shouldReturnTrue() {
        Long commentId = 1L;
        String username = "john";
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(likeRepo.existsByCommentAndUser(comment, user)).thenReturn(true);

        assertTrue(commentLikeService.hasUserLiked(commentId, username));
    }

    @Test
    @DisplayName("Should return false if user disliked")
    void hasUserDisliked_shouldReturnFalse() {
        Long commentId = 1L;
        String username = "john";
        Comment comment = mock(Comment.class);
        User user = mock(User.class);

        when(userValidator.validateUserByUsername(username)).thenReturn(user);
        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(dislikeRepo.existsByCommentAndUser(comment, user)).thenReturn(false);

        assertFalse(commentLikeService.hasUserDisliked(commentId, username));
    }

    @Test
    @DisplayName("Should return count of dislikes")
    void countDislikes_shouldReturnCount() {
        Long commentId = 1L;
        Comment comment = mock(Comment.class);
        when(commentValidator.validateCommentId(commentId)).thenReturn(comment);
        when(dislikeRepo.countByComment(comment)).thenReturn(2L);

        long count = commentLikeService.countDislikes(commentId);

        assertEquals(2L, count);
    }

    @Test
    @DisplayName("Should return false when username is null for like")
    void hasUserLiked_shouldReturnFalse_whenUsernameIsNull() {
        assertFalse(commentLikeService.hasUserLiked(1L, null));
    }

    @Test
    @DisplayName("Should return false when username is null for dislike")
    void hasUserDisliked_shouldReturnFalse_whenUsernameIsNull() {
        assertFalse(commentLikeService.hasUserDisliked(1L, null));
    }

    @Test
    @DisplayName("Should do nothing if username is null for toggleLike")
    void toggleLike_shouldDoNothing_whenUsernameIsNull() {
        commentLikeService.toggleLike(1L, null);
        verifyNoInteractions(commentValidator, userValidator, likeRepo, dislikeRepo);
    }

    @Test
    @DisplayName("Should do nothing if username is null for toggleDislike")
    void toggleDislike_shouldDoNothing_whenUsernameIsNull() {
        commentLikeService.toggleDislike(1L, null);
        verifyNoInteractions(commentValidator, userValidator, likeRepo, dislikeRepo);
    }
}
