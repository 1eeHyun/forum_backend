//package com.example.forum.service.reaction.post;
//
//import com.example.forum.dto.like.LikeUserDTO;
//import com.example.forum.mapper.post.PostMapper;
//import com.example.forum.model.like.PostReaction;
//import com.example.forum.model.post.Post;
//import com.example.forum.model.profile.Profile;
//import com.example.forum.model.user.User;
//import com.example.forum.repository.like.PostReactionRepository;
//import com.example.forum.service.notification.NotificationHelper;
//import com.example.forum.validator.auth.AuthValidator;
//import com.example.forum.validator.post.PostValidator;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockedStatic;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class PostLikeServiceImplTest {
//
//    @InjectMocks
//    private PostReactionServiceImpl postLikeService;
//
//    @Mock private AuthValidator userValidator;
//
//    @Mock private PostValidator postValidator;
//
//    @Mock private PostReactionRepository postLikeRepository;
//
//    @Mock private NotificationHelper notificationHelper;
//
//
//    @Test
//    @DisplayName("Should like a post when not already liked")
//    void toggleLike_shouldLike_whenNotLiked() {
//        Long postId = 1L;
//        String username = "alice";
//
//        // Mocks
//        User user = mock(User.class);
//        Profile profile = mock(Profile.class);
//        Post post = mock(Post.class);
//
//        // Stubbing profile and nickname
//        when(user.getProfile()).thenReturn(profile);
//        when(profile.getNickname()).thenReturn("Alice");
//
//        // Stubbing validators and repositories
//        when(userValidator.validateUserByUsername(username)).thenReturn(user);
//        when(postValidator.validatePost(postId)).thenReturn(post);
//        when(postLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.empty());
//
//        // Act
//        postLikeService.toggleLike(postId, username);
//
//        // Assert
//        verify(postLikeRepository).save(any(PostReaction.class));
//    }
//
//    @Test
//    @DisplayName("Should unlike a post when already liked")
//    void toggleLike_shouldUnlike_whenAlreadyLiked() {
//        Long postId = 1L;
//        String username = "alice";
//        User user = mock(User.class);
//        Post post = mock(Post.class);
//        PostReaction existingLike = mock(PostReaction.class);
//        List<PostReaction> likeList = mock(List.class);
//
//        when(userValidator.validateUserByUsername(username)).thenReturn(user);
//        when(postValidator.validatePost(postId)).thenReturn(post);
//        when(post.getLikes()).thenReturn(likeList);
//        when(postLikeRepository.findByPostAndUser(post, user)).thenReturn(Optional.of(existingLike));
//
//        postLikeService.toggleLike(postId, username);
//
//        verify(likeList).remove(existingLike);
//        verify(postLikeRepository).deleteByPostAndUser(post, user);
//    }
//
//    @Test
//    @DisplayName("Should return count of likes for a post")
//    void countLikes_shouldReturnCorrectCount() {
//        Long postId = 1L;
//        Post post = mock(Post.class);
//
//        when(postValidator.validatePost(postId)).thenReturn(post);
//        when(postLikeRepository.countByPost(post)).thenReturn(42L);
//
//        long count = postLikeService.countLikes(postId);
//
//        assertEquals(42L, count);
//    }
//
//    @Test
//    @DisplayName("Should return list of LikeUserDTO from post likes")
//    void getLikeUsers_shouldReturnDTOList() {
//        Long postId = 1L;
//        Post post = mock(Post.class);
//        PostReaction like1 = mock(PostReaction.class);
//        PostReaction like2 = mock(PostReaction.class);
//
//        // User & Profile mock
//        User user1 = mock(User.class);
//        User user2 = mock(User.class);
//        Profile profile1 = mock(Profile.class);
//        Profile profile2 = mock(Profile.class);
//
//        // 설정
//        when(postValidator.validatePost(postId)).thenReturn(post);
//        when(postLikeRepository.findByPost(post)).thenReturn(List.of(like1, like2));
//
//        when(like1.getUser()).thenReturn(user1);
//        when(like2.getUser()).thenReturn(user2);
//
//        when(user1.getUsername()).thenReturn("user1");
//        when(user2.getUsername()).thenReturn("user2");
//        when(user1.getProfile()).thenReturn(profile1);
//        when(user2.getProfile()).thenReturn(profile2);
//
//        when(profile1.getNickname()).thenReturn("User One");
//        when(profile2.getNickname()).thenReturn("User Two");
//        when(profile1.getImageUrl()).thenReturn("url1");
//        when(profile2.getImageUrl()).thenReturn("url2");
//        when(profile1.getImagePositionX()).thenReturn(0.1);
//        when(profile1.getImagePositionY()).thenReturn(0.2);
//        when(profile2.getImagePositionX()).thenReturn(0.3);
//        when(profile2.getImagePositionY()).thenReturn(0.4);
//
//        try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
//            mocked.when(() -> PostMapper.toLikeUserDTO(like1)).thenCallRealMethod();
//            mocked.when(() -> PostMapper.toLikeUserDTO(like2)).thenCallRealMethod();
//
//            List<LikeUserDTO> result = postLikeService.getLikeUsers(postId);
//
//            assertEquals(2, result.size());
//
//            LikeUserDTO dto1 = result.get(0);
//            assertEquals("user1", dto1.getUsername());
//            assertEquals("User One", dto1.getNickname());
//            assertEquals("url1", dto1.getImageDTO().getImageUrl());
//            assertEquals(0.1, dto1.getImageDTO().getImagePositionX());
//            assertEquals(0.2, dto1.getImageDTO().getImagePositionY());
//
//            LikeUserDTO dto2 = result.get(1);
//            assertEquals("user2", dto2.getUsername());
//            assertEquals("User Two", dto2.getNickname());
//            assertEquals("url2", dto2.getImageDTO().getImageUrl());
//            assertEquals(0.3, dto2.getImageDTO().getImagePositionX());
//            assertEquals(0.4, dto2.getImageDTO().getImagePositionY());
//        }
//    }
//
//}
