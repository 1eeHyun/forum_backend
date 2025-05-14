package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.model.comment.Comment;
import com.example.forum.model.post.Post;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.comment.CommentRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.comment.CommentValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @InjectMocks
    private CommentServiceImpl commentService;

    @Mock private CommentValidator commentValidator;
    @Mock private CommentRepository commentRepository;
    @Mock private PostValidator postValidator;
    @Mock private AuthValidator userValidator;

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
        assertEquals("tester-nickname", response.getAuthorNickname());
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
        assertEquals("tester-nickname", response.getAuthorNickname());
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
}
