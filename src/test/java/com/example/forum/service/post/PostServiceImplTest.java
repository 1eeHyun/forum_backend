package com.example.forum.service.post;

import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.exception.post.TooManyPostImagesException;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostImageRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.common.S3Service;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock private AuthValidator authValidator;
    @Mock private PostRepository postRepository;
    @Mock private PostValidator postValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private CategoryValidator categoryValidator;
    @Mock private PostImageRepository postImageRepository;
    @Mock
    private RecentViewService recentViewService;

    @Mock private S3Service s3Service;

    private User user;
    private Post post;
    private PostRequestDTO postRequestDTO;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        Profile profile = Profile.builder()
                .nickname("John")
                .imageUrl("image.jpg")
                .imagePositionX(0.5)
                .imagePositionY(0.5)
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .profile(profile)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .author(user)
                .visibility(Visibility.PUBLIC)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .build();



        postRequestDTO = PostRequestDTO.builder()
                .title("New Title")
                .content("New Content")
                .visibility(Visibility.PUBLIC)
                .build();
    }

    @Nested
    @DisplayName("createPost()")
    class CreatePostTest {

        @Test
        @DisplayName("Success - Create public post without community")
        void success_create_public_post() {
            when(authValidator.validateUserByUsername(anyString())).thenReturn(user);

            when(postRepository.save(any(Post.class))).thenAnswer(invocation -> {
                Post p = invocation.getArgument(0);
                p.setId(1L); // Optional: ID set
                return p; // return actual post with updated title
            });

            PostResponseDTO response = postService.createPost(postRequestDTO, "john");

            assertThat(response.getTitle()).isEqualTo("New Title");
            verify(postRepository).save(any(Post.class));
        }

        @Test
        @DisplayName("Success - Create community post")
        void success_create_community_post() {
            // given
            postRequestDTO = PostRequestDTO.builder()
                    .title("Title")
                    .content("Content")
                    .visibility(Visibility.COMMUNITY)
                    .categoryId(1L)
                    .build();

            Community community = mock(Community.class);
            Category category = mock(Category.class);

            when(authValidator.validateUserByUsername(anyString())).thenReturn(user);
            when(categoryValidator.validateCategoryById(anyLong())).thenReturn(category);
            when(category.getCommunity()).thenReturn(community);

            when(community.getId()).thenReturn(1L);

            when(communityValidator.validateMemberCommunity(anyLong(), any()))
                    .thenReturn(community);

            when(postRepository.save(any())).thenReturn(post);

            // when
            PostResponseDTO result = postService.createPost(postRequestDTO, "john");

            // then
            assertThat(result).isNotNull();
            verify(communityValidator).validateMemberCommunity(anyLong(), any());
        }


        @Test
        @DisplayName("Failure - Too many images in post")
        void fail_too_many_images() {
            List<String> images = List.of("1.jpg", "2.jpg", "3.jpg", "4.jpg", "5.jpg", "6.jpg");

            postRequestDTO = PostRequestDTO.builder()
                    .title("Too many images")
                    .content("...")
                    .visibility(Visibility.PUBLIC)
                    .imageUrls(images)
                    .build();

            when(authValidator.validateUserByUsername(anyString())).thenReturn(user);

            doThrow(new TooManyPostImagesException()).when(postValidator).validatePostCount(any());

            assertThatThrownBy(() -> postService.createPost(postRequestDTO, "john"))
                    .isInstanceOf(TooManyPostImagesException.class);
        }
    }

    @Nested
    @DisplayName("getPostDetail()")
    class GetPostDetailTest {

        @Test
        @DisplayName("Success - Get post detail with logged in user")
        void success_get_detail_with_user() {
            when(authValidator.validateUserByUsername(anyString())).thenReturn(user);
            when(postValidator.validateDetailPostId(anyLong())).thenReturn(post);

            PostDetailDTO detail = postService.getPostDetail(1L, "john");

            assertThat(detail).isNotNull();
        }

        @Test
        @DisplayName("Success - Get post detail without user")
        void success_get_detail_without_user() {
            when(postValidator.validateDetailPostId(anyLong())).thenReturn(post);

            PostDetailDTO detail = postService.getPostDetail(1L, null);

            assertThat(detail).isNotNull();
        }

        @Test
        @DisplayName("Failure - Post not found")
        void fail_post_not_found() {
            when(postValidator.validateDetailPostId(anyLong())).thenThrow(new RuntimeException("Not Found"));

            assertThatThrownBy(() -> postService.getPostDetail(999L, "john"))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("updatePost()")
    class UpdatePostTest {

        @Test
        @DisplayName("Success - Update existing post")
        void success_update_post() {
            when(authValidator.validateUserByUsername(anyString())).thenReturn(user);
            when(postValidator.validatePost(anyLong())).thenReturn(post);
            doNothing().when(postValidator).validatePostAuthor(any(), any());

            // Return the updated post argument directly
            when(postRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

            // postRequestDTO must have "New Title"
            PostRequestDTO postRequestDTO = PostRequestDTO.builder()
                    .title("New Title")
                    .content("Updated content")
                    .visibility(Visibility.PUBLIC)
                    .imageUrls(List.of())
                    .build();

            PostResponseDTO result = postService.updatePost(1L, postRequestDTO, "john");

            assertThat(result.getTitle()).isEqualTo("New Title");
        }

        @Test
        @DisplayName("Failure - Not post author")
        void fail_update_not_author() {
            User anotherUser = User.builder().id(2L).build();

            when(authValidator.validateUserByUsername(anyString())).thenReturn(anotherUser);
            when(postValidator.validatePost(anyLong())).thenReturn(post);
            doThrow(new RuntimeException("Not Author")).when(postValidator).validatePostAuthor(any(), any());

            assertThatThrownBy(() -> postService.updatePost(1L, postRequestDTO, "notAuthor"))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("deletePost()")
    class DeletePostTest {

        @Test
        @DisplayName("Success - Delete own post")
        void success_delete_post() {
            when(authValidator.validateUserByUsername(anyString())).thenReturn(user);
            when(postValidator.validatePost(anyLong())).thenReturn(post);
            doNothing().when(postValidator).validatePostAuthor(post, user);

            postService.deletePost(1L, "john");

            verify(postRepository).delete(post);
        }

        @Test
        @DisplayName("Failure - Unauthorized user")
        void fail_delete_not_author() {
            User anotherUser = User.builder().id(2L).build();

            when(authValidator.validateUserByUsername(anyString())).thenReturn(anotherUser);
            when(postValidator.validatePost(anyLong())).thenReturn(post);
            doThrow(new RuntimeException("Not Author")).when(postValidator).validatePostAuthor(post, anotherUser);

            assertThatThrownBy(() -> postService.deletePost(1L, "notAuthor"))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("uploadImage()")
    class UploadImageTest {

        @Test
        @DisplayName("Success - Upload image to S3")
        void success_upload_image() {
            MultipartFile mockFile = mock(MultipartFile.class);
            when(s3Service.upload(mockFile)).thenReturn("https://s3.com/image.jpg");

            String url = postService.uploadImage(mockFile);

            assertThat(url).isEqualTo("https://s3.com/image.jpg");
        }
    }
}
