package com.example.forum.service.post;

import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private AuthValidator authValidator;

    @Mock
    private PostRepository postRepository;

    @Mock
    private PostValidator postValidator;

    @Mock
    private CommunityValidator communityValidator;

    private User mockUser;
    private Post mockPost;
    private Community mockCommunity;

    @BeforeEach
    void setUp() {
        Profile profile = Profile.builder()
                .nickname("tester")
                .build();

        mockUser = User.builder()
                .id(1L)
                .username("tester")
                .email("test@example.com")
                .profile(profile)
                .build();
        profile.setUser(mockUser);

        mockCommunity = Community.builder()
                .id(100L)
                .name("Dev Community")
                .build();

        mockPost = Post.builder()
                .id(1L)
                .title("Title")
                .content("Content")
                .author(mockUser)
                .visibility(Visibility.PUBLIC)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .comments(new ArrayList<>())
                .likes(new ArrayList<>())
                .build();
    }

    @Test
    void createPost_withPublicVisibility_shouldReturnCreatedPost() {
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Visibility.PUBLIC, null);

        when(authValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        var result = postService.createPost(dto, "tester");

        assertEquals("Title", result.getTitle());
        assertEquals("Content", result.getContent());
        assertEquals("tester", result.getAuthorNickname());
    }

    @Test
    void createPost_withCommunityVisibility_shouldReturnCreatedPost() {
        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Visibility.COMMUNITY, 100L);

        when(authValidator.validateUserByUsername("tester")).thenReturn(mockUser);
        when(communityValidator.validateMemberCommunity(100L, mockUser)).thenReturn(mockCommunity);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        var result = postService.createPost(dto, "tester");

        assertEquals("Title", result.getTitle());
        assertEquals("tester", result.getAuthorNickname());
    }

    @Test
    void updatePost_shouldModifyPostAndReturnUpdatedDto() {
        PostRequestDTO dto = new PostRequestDTO("New Title", "New Content", Visibility.PRIVATE, null);

        when(postValidator.validatePostAuthor(1L, "tester")).thenReturn(mockPost);
        when(postRepository.save(any(Post.class))).thenReturn(mockPost);

        var result = postService.updatePost(1L, dto, "tester");

        assertEquals("New Title", result.getTitle());
        assertEquals("New Content", result.getContent());
    }

    @Test
    void deletePost_shouldCallRepositoryDelete() {
        when(postValidator.validatePostAuthor(1L, "tester")).thenReturn(mockPost);

        postService.deletePost(1L, "tester");

        verify(postRepository, times(1)).delete(mockPost);
    }

    @Test
    void getAllPostsByDESC_shouldReturnPostList() {
        when(postRepository.findAllByVisibilityOrderByCreatedAtDesc(Visibility.PUBLIC))
                .thenReturn(List.of(mockPost));

        var result = postService.getAllPublicPostsByDESC();

        assertEquals(1, result.size());
        assertEquals("Title", result.get(0).getTitle());
        assertEquals("tester", result.get(0).getAuthorNickname());
    }
}
