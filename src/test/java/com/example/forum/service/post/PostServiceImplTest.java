//package com.example.forum.service.post;
//
//import com.example.forum.common.SortOrder;
//import com.example.forum.dto.post.PostDetailDTO;
//import com.example.forum.dto.post.PostRequestDTO;
//import com.example.forum.dto.post.PostResponseDTO;
//import com.example.forum.dto.util.ImageDTO;
//import com.example.forum.mapper.post.PostMapper;
//import com.example.forum.model.community.Community;
//import com.example.forum.model.post.Post;
//import com.example.forum.model.post.Visibility;
//import com.example.forum.model.user.User;
//import com.example.forum.repository.community.CommunityMemberRepository;
//import com.example.forum.repository.post.PostImageRepository;
//import com.example.forum.repository.post.PostRepository;
//import com.example.forum.service.S3Service;
//import com.example.forum.validator.auth.AuthValidator;
//import com.example.forum.validator.community.CommunityValidator;
//import com.example.forum.validator.post.PostValidator;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PostServiceImplTest {
//
//    @InjectMocks
//    private PostServiceImpl postService;
//
//    @Mock
//    private AuthValidator authValidator;
//
//    @Mock
//    private PostRepository postRepository;
//
//    @Mock
//    private PostValidator postValidator;
//
//    @Mock
//    private CommunityValidator communityValidator;
//
//    @Mock
//    private CommunityMemberRepository communityMemberRepository;
//
//    @Mock
//    private PostImageRepository postImageRepository;
//
//    @Mock
//    private S3Service s3Service;
//
//    @Test
//    @DisplayName("Should get paged posts sorted by newest")
//    void getPagedPosts_shouldReturnList_sortedByNewest() {
//        Post post = mock(Post.class);
//        when(postRepository.findPagedPosts(anyInt(), anyInt())).thenReturn(List.of(post));
//
//        List<PostResponseDTO> result = postService.getPagedPosts(SortOrder.NEWEST, 0, 3);
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    @DisplayName("Should get top liked posts")
//    void getPagedPosts_shouldReturnList_sortedByTopLiked() {
//        Post post = mock(Post.class);
//        when(postRepository.findAllNonPrivatePostsWithLikeCountDesc()).thenReturn(List.of(post));
//
//        List<PostResponseDTO> result = postService.getPagedPosts(SortOrder.TOP_LIKED, 0, 10);
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    @DisplayName("Should get profile posts including private for same user")
//    void getProfilePosts_shouldReturnList_includingPrivate() {
//        User user = new User();
//        user.setId(1L);
//        Post post = mock(Post.class);
//
//        when(authValidator.validateUserByUsername("alice")).thenReturn(user);
//        when(postRepository.findPostsByAuthorOrderByLikeCount(user, true)).thenReturn(List.of(post));
//
//        List<PostResponseDTO> result = postService.getProfilePosts("alice", "alice", SortOrder.TOP_LIKED, 0, 10);
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    @DisplayName("Should get profile posts with pagination")
//    void getProfilePosts_shouldReturnPagedList_sortedByNewest() {
//        User user = new User();
//        user.setId(1L);
//        Post post = mock(Post.class);
//
//        Page<Post> page = new PageImpl<>(List.of(post));
//        when(authValidator.validateUserByUsername("alice")).thenReturn(user);
//        when(authValidator.validateUserByUsername("bob")).thenReturn(new User());
//        when(postRepository.findPostsByAuthor(eq(user), eq(false), any(Pageable.class))).thenReturn(page);
//
//        List<PostResponseDTO> result = postService.getProfilePosts("alice", "bob", SortOrder.NEWEST, 0, 10);
//
//        assertEquals(1, result.size());
//    }
//
//    @Test
//    @DisplayName("Should return post detail DTO")
//    void getPostDetail_shouldReturnDTO() {
//        Post post = mock(Post.class);
//        when(postValidator.validateDetailPostId(1L)).thenReturn(post);
//
//        try (MockedStatic<PostMapper> mockedMapper = mockStatic(PostMapper.class)) {
//            mockedMapper.when(() -> PostMapper.toPostDetailDTO(eq(post), any())).thenReturn(mock(PostDetailDTO.class));
//            PostDetailDTO result = postService.getPostDetail(1L, null);
//            assertNotNull(result);
//        }
//    }
//
//    @Test
//    @DisplayName("Should create post with image URLs")
//    void createPost_shouldSavePostWithImages() {
//        User user = mock(User.class);
//        Community community = mock(Community.class);
//        PostRequestDTO dto = new PostRequestDTO("Title", "Content", Visibility.COMMUNITY, 1L, List.of("url1", "url2"));
//
//        when(authValidator.validateUserByUsername("alice")).thenReturn(user);
//        when(communityValidator.validateMemberCommunity(1L, user)).thenReturn(community);
//
//        try (MockedStatic<PostMapper> mockedMapper = mockStatic(PostMapper.class)) {
//            mockedMapper.when(() -> PostMapper.toPostResponseDTO(any(Post.class))).thenReturn(mock(PostResponseDTO.class));
//
//            PostResponseDTO result = postService.createPost(dto, "alice");
//
//            assertNotNull(result);
//            verify(postRepository).save(any(Post.class));
//            verify(postImageRepository).saveAll(anyList());
//        }
//    }
//
//    @Test
//    @DisplayName("Should update existing post")
//    void updatePost_shouldUpdatePost() {
//        User user = mock(User.class);
//        Post post = Post.builder().title("Old").content("Old").build();
//        PostRequestDTO dto = new PostRequestDTO("New", "New", Visibility.PUBLIC, null, null);
//
//        when(authValidator.validateUserByUsername("alice")).thenReturn(user);
//        when(postValidator.validatePost(1L)).thenReturn(post);
//
//        try (MockedStatic<PostMapper> mockedMapper = mockStatic(PostMapper.class)) {
//            mockedMapper.when(() -> PostMapper.toPostResponseDTO(post)).thenReturn(mock(PostResponseDTO.class));
//
//            PostResponseDTO result = postService.updatePost(1L, dto, "alice");
//
//            assertEquals("New", post.getTitle());
//            assertEquals("New", post.getContent());
//            assertNotNull(result);
//            verify(postRepository).save(post);
//        }
//    }
//
//    @Test
//    @DisplayName("Should delete post")
//    void deletePost_shouldDeleteSuccessfully() {
//        User user = mock(User.class);
//        Post post = mock(Post.class);
//
//        when(authValidator.validateUserByUsername("alice")).thenReturn(user);
//        when(postValidator.validatePost(1L)).thenReturn(post);
//
//        postService.deletePost(1L, "alice");
//
//        verify(postRepository).delete(post);
//    }
//
//    @Test
//    @DisplayName("Should upload image and return URL")
//    void uploadImage_shouldReturnUrl() {
//        MultipartFile file = mock(MultipartFile.class);
//        when(s3Service.upload(file)).thenReturn("https://s3.url/image.jpg");
//
//        String result = postService.uploadImage(file);
//
//        assertEquals("https://s3.url/image.jpg", result);
//    }
//}
