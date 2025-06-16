package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.model.community.Category;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.common.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CategoryValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PostServiceImplTest {

    @Mock private AuthValidator authValidator;
    @Mock private PostValidator postValidator;
    @Mock private CategoryValidator categoryValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private PostRepository postRepository;
    @Mock private S3Service s3Service;
    @Mock private RecentViewService recentViewService;

    @InjectMocks
    private PostServiceImpl postService;

    private User user;
    private Post post;
    private Category category;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Profile profile = Profile.builder()
                .nickname("john")
                .imageUrl("profile.jpg")
                .imagePositionX(0.0)
                .imagePositionY(0.0)
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .password("pw")
                .profile(profile)
                .build();

        category = Category.builder()
                .id(1L)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .category(category)
                .visibility(Visibility.PUBLIC)
                .images(new ArrayList<>())
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("getPagedPosts() returns posts sorted by newest")
    void getPagedPosts_newest() {
        when(postRepository.findPagedPostsNewest(10, 0)).thenReturn(List.of(post));

        List<PostResponseDTO> result = postService.getPagedPosts(SortOrder.NEWEST, 0, 10);

        assertThat(result).hasSize(1);
    }

    @Test
    @DisplayName("getPostDetail() returns post details")
    void getPostDetail_success() {
        when(authValidator.validateUserByUsername("john")).thenReturn(user);
        when(postValidator.validateDetailPostId(1L)).thenReturn(post);

        PostDetailDTO result = postService.getPostDetail(1L, "john");

        assertThat(result).isNotNull();
        verify(recentViewService).addPostView(user.getId(), 1L);
    }

    @Test
    @DisplayName("createPost() saves new post")
    void createPost_success() {
        PostRequestDTO dto = PostRequestDTO.builder()
                .title("New Title")
                .content("New Content")
                .visibility(Visibility.PUBLIC)
                .imageUrls(new ArrayList<>())
                .build();

        when(authValidator.validateUserByUsername("john")).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);

        PostResponseDTO result = postService.createPost(dto, "john");

        assertThat(result).isNotNull();
        verify(postRepository).save(any(Post.class));
    }

    @Test
    @DisplayName("updatePost() updates and saves post")
    void updatePost_success() {
        PostRequestDTO dto = PostRequestDTO.builder()
                .title("Updated")
                .content("Updated content")
                .visibility(Visibility.PUBLIC)
                .imageUrls(List.of("url1"))
                .build();

        when(authValidator.validateUserByUsername("john")).thenReturn(user);
        when(postValidator.validatePost(1L)).thenReturn(post);
        doNothing().when(postValidator).validatePostAuthor(post, user);
        doNothing().when(s3Service).deleteFiles(any());
        when(postRepository.save(any(Post.class))).thenReturn(post);

        post.setImages(new ArrayList<>()); // Ensure images is mutable

        PostResponseDTO result = postService.updatePost(1L, dto, "john");

        assertThat(result).isNotNull();
        verify(s3Service).deleteFiles(any());
    }

    @Test
    @DisplayName("deletePost() deletes post")
    void deletePost_success() {
        when(authValidator.validateUserByUsername("john")).thenReturn(user);
        when(postValidator.validatePost(1L)).thenReturn(post);
        doNothing().when(postValidator).validatePostAuthor(post, user);

        postService.deletePost(1L, "john");

        verify(postRepository).delete(post);
    }
}