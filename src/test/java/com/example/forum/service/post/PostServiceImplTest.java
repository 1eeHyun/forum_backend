//package com.example.forum.service.post;
//
//import com.example.forum.dto.post.PostRequestDTO;
//import com.example.forum.dto.post.PostResponseDTO;
//import com.example.forum.model.community.Community;
//import com.example.forum.model.post.Post;
//import com.example.forum.model.post.Visibility;
//import com.example.forum.model.profile.Profile;
//import com.example.forum.model.user.User;
//import com.example.forum.repository.community.CommunityRepository;
//import com.example.forum.repository.post.PostRepository;
//import com.example.forum.validator.auth.AuthValidator;
//import com.example.forum.validator.community.CommunityValidator;
//import com.example.forum.validator.post.PostValidator;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertThrows;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//@DisplayName("PostServiceImpl Unit Tests")
//class PostServiceImplTest {
//
//    @Mock private AuthValidator authValidator;
//    @Mock private PostRepository postRepository;
//    @Mock private PostValidator postValidator;
//    @Mock private CommunityValidator communityValidator;
//    @Mock private CommunityRepository communityRepository;
//
//    @InjectMocks
//    private PostServiceImpl postService;
//
//    @Test
//    @DisplayName("Create public post successfully")
//    void testCreatePublicPostSuccess() {
//        User user = createTestUser();
//        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Visibility.PUBLIC, null);
//
//        when(authValidator.validateUserByUsername("john")).thenReturn(user);
//        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        PostResponseDTO result = postService.createPost(dto, "john");
//
//        assertEquals("Title", result.getTitle());
//        assertEquals("Content", result.getContent());
//        assertEquals("tester-nickname", result.getAuthor().getNickname());
//    }
//
//    @Test
//    @DisplayName("Create community-only post successfully")
//    void testCreateCommunityPostSuccess() {
//        User user = createTestUser();
//        Community community = new Community();
//        PostRequestDTO dto = new PostRequestDTO("CommTitle", "CommContent", Visibility.COMMUNITY, 1L);
//
//        when(authValidator.validateUserByUsername("john")).thenReturn(user);
//        when(communityValidator.validateMemberCommunity(1L, user)).thenReturn(community);
//        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        PostResponseDTO result = postService.createPost(dto, "john");
//
//        assertEquals("CommTitle", result.getTitle());
//        assertEquals("CommContent", result.getContent());
//    }
//
//    @Test
//    @DisplayName("Fail to create community-only post: user not member")
//    void testCreateCommunityPostFail() {
//        User user = createTestUser();
//        PostRequestDTO dto = new PostRequestDTO("FailTitle", "FailContent", Visibility.COMMUNITY, 999L);
//
//        when(authValidator.validateUserByUsername("john")).thenReturn(user);
//        when(communityValidator.validateMemberCommunity(999L, user))
//                .thenThrow(new IllegalArgumentException("User is not a member of the community"));
//
//        Exception exception = assertThrows(IllegalArgumentException.class,
//                () -> postService.createPost(dto, "john"));
//
//        assertEquals("User is not a member of the community", exception.getMessage());
//    }
//
//    @Test
//    @DisplayName("Update post successfully")
//    void testUpdatePostSuccess() {
//        User user = createTestUser();
//        Post post = Post.builder()
//                .id(1L)
//                .title("Old Title")
//                .content("Old Content")
//                .author(user)
//                .build();
//
//        PostRequestDTO dto = new PostRequestDTO("Updated Title", "Updated Content", Visibility.PUBLIC, null);
//
//        when(postValidator.validatePostAuthor(1L, "john")).thenReturn(post);
//        when(postRepository.save(any(Post.class))).thenAnswer(inv -> inv.getArgument(0));
//
//        PostResponseDTO result = postService.updatePost(1L, dto, "john");
//
//        assertEquals("Updated Title", result.getTitle());
//        assertEquals("Updated Content", result.getContent());
//        assertEquals("tester-nickname", result.getAuthor().getNickname());
//    }
//
//    @Test
//    @DisplayName("Fail to update post: not the author")
//    void testUpdatePostFail() {
//        PostRequestDTO dto = new PostRequestDTO("Fail", "Fail", Visibility.PUBLIC, null);
//
//        when(postValidator.validatePostAuthor(1L, "john"))
//                .thenThrow(new IllegalArgumentException("Not the author"));
//
//        assertThrows(IllegalArgumentException.class,
//                () -> postService.updatePost(1L, dto, "john"));
//    }
//
//    @Test
//    @DisplayName("Delete post successfully")
//    void testDeletePostSuccess() {
//        Post post = Post.builder().id(1L).build();
//
//        when(postValidator.validatePostAuthor(1L, "john")).thenReturn(post);
//
//        postService.deletePost(1L, "john");
//
//        verify(postRepository, times(1)).delete(post);
//    }
//
//    @Test
//    @DisplayName("Fail to delete post: not the author")
//    void testDeletePostFail() {
//        when(postValidator.validatePostAuthor(1L, "john"))
//                .thenThrow(new IllegalArgumentException("Not the author"));
//
//        assertThrows(IllegalArgumentException.class,
//                () -> postService.deletePost(1L, "john"));
//    }
//
//    @Test
//    @DisplayName("Get public posts in ASC order when not logged in")
//    void testGetPublicPostsASC() {
//        User user = createTestUser();
//        Post post = Post.builder()
//                .title("Title")
//                .author(user)
//                .build();
//
//        when(postRepository.findAllByVisibilityOrderByCreatedAtAsc(Visibility.PUBLIC))
//                .thenReturn(List.of(post));
//
//        List<PostResponseDTO> result = postService.getAccessiblePostsByASC(null);
//
//        assertEquals(1, result.size());
//        assertEquals("Title", result.get(0).getTitle());
//    }
//
//    @Test
//    @DisplayName("Get accessible posts by DESC for logged-in user")
//    void testGetAccessiblePostsDESC_User() {
//        User user = createTestUser();
//        Community community = new Community();
//        Post post = Post.builder()
//                .title("Latest Post")
//                .author(user)
//                .build();
//
//        when(authValidator.validateUserByUsername("john")).thenReturn(user);
//        when(communityRepository.findAllByMembersContaining(user)).thenReturn(List.of(community));
//        when(postRepository.findAccessiblePosts(List.of(community))).thenReturn(List.of(post));
//
//        List<PostResponseDTO> result = postService.getAccessiblePostsByDESC("john");
//
//        assertEquals(1, result.size());
//        assertEquals("Latest Post", result.get(0).getTitle());
//    }
//
//    private User createTestUser() {
//        Profile profile = Profile.builder()
//                .nickname("tester-nickname")
//                .build();
//
//        return User.builder()
//                .username("tester")
//                .profile(profile)
//                .build();
//    }
//}
