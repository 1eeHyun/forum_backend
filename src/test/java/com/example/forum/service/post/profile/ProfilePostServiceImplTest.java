package com.example.forum.service.post.profile;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.post.HiddenPostRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ProfilePostServiceImpl.
 * - Covers sorting, private post inclusion, and hidden/favorite flags.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ProfilePostServiceImpl")
class ProfilePostServiceImplTest {

    @InjectMocks
    private ProfilePostServiceImpl service;

    @Mock private AuthValidator authValidator;
    @Mock private PostRepository postRepository;
    @Mock private HiddenPostRepository hiddenPostRepository;
    @Mock private CommunityFavoriteRepository communityFavoriteRepository;

    @Nested
    @DisplayName("getProfilePosts")
    class GetProfilePosts {

        private final String targetUsername = "target";
        private final String currentUsername = "current";

        private User targetUser(long id) {
            User u = mock(User.class);
            when(u.getId()).thenReturn(id);
            return u;
        }

        @Test
        @DisplayName("Should fetch posts with NEWEST sort and includePrivate=false")
        void newestSort_notOwner() {
            User target = targetUser(1L);
            User current = targetUser(2L);

            when(authValidator.validateUserByUsername(targetUsername)).thenReturn(target);
            when(authValidator.validateUserByUsername(currentUsername)).thenReturn(current);

            when(hiddenPostRepository.findHiddenPostIdsByUser(current)).thenReturn(List.of(101L));
            when(communityFavoriteRepository.findAllByUser(current)).thenReturn(List.of());

            Post p1 = mock(Post.class);
            when(p1.getId()).thenReturn(100L);
            when(p1.getCategory()).thenReturn(null); // no community

            Page<Post> pageData = new PageImpl<>(List.of(p1));
            when(postRepository.findPostsByAuthor(eq(target), eq(false), any(Pageable.class))).thenReturn(pageData);

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dto = mock(PostResponseDTO.class);
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(p1), eq(false), eq(false))).thenReturn(dto);

                List<PostResponseDTO> result = service.getProfilePosts(targetUsername, currentUsername, SortOrder.NEWEST, 0, 10);

                assertEquals(List.of(dto), result);
                // verify sorting by createdAt desc
                ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findPostsByAuthor(eq(target), eq(false), captor.capture());
                assertTrue(captor.getValue().getSort().getOrderFor("createdAt").isDescending());
            }
        }

        @Test
        @DisplayName("Should fetch posts with OLDEST sort and includePrivate=true for same user")
        void oldestSort_owner() {
            User target = targetUser(5L);
            User current = targetUser(5L); // same id => owner

            when(authValidator.validateUserByUsername(targetUsername)).thenReturn(target);
            when(authValidator.validateUserByUsername(currentUsername)).thenReturn(current);

            when(hiddenPostRepository.findHiddenPostIdsByUser(current)).thenReturn(List.of());
            when(communityFavoriteRepository.findAllByUser(current)).thenReturn(List.of());

            Post p1 = mock(Post.class);
            when(p1.getId()).thenReturn(200L);
            when(p1.getCategory()).thenReturn(null);

            Page<Post> pageData = new PageImpl<>(List.of(p1));
            when(postRepository.findPostsByAuthor(eq(target), eq(true), any(Pageable.class))).thenReturn(pageData);

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dto = mock(PostResponseDTO.class);
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(p1), eq(false), eq(false))).thenReturn(dto);

                List<PostResponseDTO> result = service.getProfilePosts(targetUsername, currentUsername, SortOrder.OLDEST, 1, 5);

                assertEquals(List.of(dto), result);
                // verify sorting by createdAt asc
                ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findPostsByAuthor(eq(target), eq(true), captor.capture());
                assertTrue(captor.getValue().getSort().getOrderFor("createdAt").isAscending());
            }
        }

        @Test
        @DisplayName("Should fetch posts with TOP_LIKED sort and apply hidden/favorite flags")
        void topLikedSort_withFlags() {
            User target = targetUser(10L);
            User current = targetUser(99L);

            when(authValidator.validateUserByUsername(targetUsername)).thenReturn(target);
            when(authValidator.validateUserByUsername(currentUsername)).thenReturn(current);

            // hidden id matches p1
            when(hiddenPostRepository.findHiddenPostIdsByUser(current)).thenReturn(List.of(301L));
            // favorite community matches c1 id
            Community favCommunity = mock(Community.class);
            when(favCommunity.getId()).thenReturn(77L);
            var favEntity = mock(com.example.forum.model.community.CommunityFavorite.class);
            when(favEntity.getCommunity()).thenReturn(favCommunity);
            when(communityFavoriteRepository.findAllByUser(current)).thenReturn(List.of(favEntity));

            Post p1 = mock(Post.class);
            when(p1.getId()).thenReturn(301L);
            Category cat1 = mock(Category.class);
            when(cat1.getCommunity()).thenReturn(favCommunity);
            when(p1.getCategory()).thenReturn(cat1);

            Post p2 = mock(Post.class);
            when(p2.getId()).thenReturn(400L);
            when(p2.getCategory()).thenReturn(null); // no community

            Page<Post> pageData = new PageImpl<>(List.of(p1, p2));
            when(postRepository.findPostsByAuthorWithLikeCount(eq(target), eq(false), any(Pageable.class))).thenReturn(pageData);

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dto1 = mock(PostResponseDTO.class);
                PostResponseDTO dto2 = mock(PostResponseDTO.class);
                // p1: hidden=true, favorite=true
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(p1), eq(true), eq(true))).thenReturn(dto1);
                // p2: hidden=false, favorite=false
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(p2), eq(false), eq(false))).thenReturn(dto2);

                List<PostResponseDTO> result = service.getProfilePosts(targetUsername, currentUsername, SortOrder.TOP_LIKED, 0, 5);

                assertEquals(List.of(dto1, dto2), result);
                // TOP_LIKED sort has no sort on Pageable
                ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findPostsByAuthorWithLikeCount(eq(target), eq(false), captor.capture());
                assertFalse(captor.getValue().getSort().isSorted());
            }
        }
    }
}
