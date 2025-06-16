package com.example.forum.service.post.profile;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class ProfilePostServiceImplTest {

    @Mock private AuthValidator authValidator;
    @Mock private PostRepository postRepository;

    @InjectMocks
    private ProfilePostServiceImpl profilePostService;

    private User user;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Profile profile = Profile.builder()
                .nickname("John")
                .imageUrl("img.jpg")
                .imagePositionX(0.5)
                .imagePositionY(0.5)
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .password("pw")
                .profile(profile)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Hello")
                .content("World")
                .author(user)
                .visibility(Visibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getProfilePosts()")
    class GetProfilePostsTest {

        @Test
        @DisplayName("Success: Current user is target user (include private)")
        void success_includePrivate() {
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            Page<Post> page = new PageImpl<>(List.of(post));
            when(postRepository.findPostsByAuthor(eq(user), eq(true), any(Pageable.class)))
                    .thenReturn(page);

            List<PostResponseDTO> result = profilePostService.getProfilePosts("john", "john", SortOrder.NEWEST, 0, 10);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Success: Current user is not target user (exclude private)")
        void success_excludePrivate() {
            User viewer = User.builder().id(2L).username("guest").build();

            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(authValidator.validateUserByUsername("guest")).thenReturn(viewer);
            Page<Post> page = new PageImpl<>(List.of(post));
            when(postRepository.findPostsByAuthor(eq(user), eq(false), any(Pageable.class)))
                    .thenReturn(page);

            List<PostResponseDTO> result = profilePostService.getProfilePosts("john", "guest", SortOrder.NEWEST, 0, 10);

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Success: Sort by top liked")
        void success_topLiked() {
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            Page<Post> page = new PageImpl<>(List.of(post));
            when(postRepository.findPostsByAuthorWithLikeCount(eq(user), eq(true), any(Pageable.class)))
                    .thenReturn(page);

            List<PostResponseDTO> result = profilePostService.getProfilePosts("john", "john", SortOrder.TOP_LIKED, 0, 10);

            assertThat(result).hasSize(1);
        }
    }
}
