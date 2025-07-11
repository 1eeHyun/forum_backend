package com.example.forum.service.post;

import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.common.S3Service;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CategoryValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private PostValidator postValidator;

    @Mock
    private CategoryValidator categoryValidator;

    @Mock
    private CommunityValidator communityValidator;

    @Mock
    private HiddenPostService hiddenPostService;

    @Mock
    private S3Service s3Service;

    @Mock
    private RecentViewService recentViewService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Profile profile = Profile.builder()
                .nickname("John")
                .imageUrl("https://example.com/profile.jpg")
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .profile(profile)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .visibility(Visibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .likes(List.of())
                .comments(List.of())
                .build();
    }

    @Nested
    @DisplayName("getPostDetail()")
    class GetPostDetail {

        @Test
        @DisplayName("should return post detail when username is given")
        void shouldReturnPostDetailWithViewer() {
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(postValidator.validateDetailPostId(1L)).thenReturn(post);

            PostDetailDTO result = postService.getPostDetail(1L, "john");

            assertThat(result.getId()).isEqualTo(1L);
            verify(recentViewService).addPostView(1L, 1L);
        }

        @Test
        @DisplayName("should return post detail when username is null")
        void shouldReturnPostDetailWithoutViewer() {
            when(postValidator.validateDetailPostId(1L)).thenReturn(post);

            PostDetailDTO result = postService.getPostDetail(1L, null);

            assertThat(result.getId()).isEqualTo(1L);
            verify(recentViewService, never()).addPostView(any(), any());
        }
    }

    @Nested
    @DisplayName("createPost()")
    class CreatePost {

        @Test
        @DisplayName("should create and return new post")
        void shouldCreatePost() {
            PostRequestDTO dto = PostRequestDTO.builder()
                    .title("New Post")
                    .content("Post Content")
                    .visibility(Visibility.PUBLIC)
                    .build();

            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(postRepository.save(any(Post.class))).thenReturn(post);

            PostResponseDTO result = postService.createPost(dto, "john");

            assertThat(result.getTitle()).isEqualTo("Test Title");
            verify(postRepository).save(any(Post.class));
        }
    }

    @Nested
    @DisplayName("deletePost()")
    class DeletePost {

        @Test
        @DisplayName("should delete post if user is author")
        void shouldDeletePost() {
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(postValidator.validatePost(1L)).thenReturn(post);

            post.setAuthor(user); // 유저가 작성자임
            postService.deletePost(1L, "john");

            verify(postRepository).delete(post);
        }
    }
}
