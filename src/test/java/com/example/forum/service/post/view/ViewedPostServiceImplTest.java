package com.example.forum.service.post.view;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ViewedPostServiceImpl.
 * - Covers getRecentlyViewedPosts and getPreviewPostsByIds methods.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ViewedPostServiceImpl")
class ViewedPostServiceImplTest {

    @InjectMocks
    private ViewedPostServiceImpl service;

    @Mock private AuthValidator authValidator;
    @Mock private RecentViewService recentViewService;
    @Mock private PostRepository postRepository;
    @Mock private HiddenPostService hiddenPostService;

    @Nested
    @DisplayName("getRecentlyViewedPosts")
    class GetRecentlyViewedPosts {

        @Test
        @DisplayName("Should return empty list when recent IDs list is empty")
        void emptyRecentIds_returnsEmptyList() {
            String username = "alice";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(user.getId()).thenReturn(1L);

            when(recentViewService.getRecentPostIds(1L)).thenReturn(List.of());

            List<PostPreviewDTO> result = service.getRecentlyViewedPosts(username);

            assertNotNull(result);
            assertTrue(result.isEmpty());
            verifyNoInteractions(postRepository, hiddenPostService);
        }

        @Test
        @DisplayName("Should map posts in the order of recent IDs and apply hidden flags")
        void mapsPostsWithOrderAndHiddenFlags() {
            String username = "bob";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(user.getId()).thenReturn(42L);

            List<Long> ids = List.of(10L, 20L, 30L);
            when(recentViewService.getRecentPostIds(42L)).thenReturn(ids);

            // Prepare Post entities
            Post p10 = mock(Post.class); when(p10.getId()).thenReturn(10L);
            Post p20 = mock(Post.class); when(p20.getId()).thenReturn(20L);
            Post p30 = mock(Post.class); when(p30.getId()).thenReturn(30L);

            // Return posts in shuffled order to check reordering
            when(postRepository.findAllById(ids)).thenReturn(List.of(p20, p10, p30));

            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of(20L));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostPreviewDTO dto10 = mock(PostPreviewDTO.class);
                PostPreviewDTO dto20 = mock(PostPreviewDTO.class);
                PostPreviewDTO dto30 = mock(PostPreviewDTO.class);

                mocked.when(() -> PostMapper.toPreviewDTO(p10, false)).thenReturn(dto10);
                mocked.when(() -> PostMapper.toPreviewDTO(p20, true)).thenReturn(dto20);
                mocked.when(() -> PostMapper.toPreviewDTO(p30, false)).thenReturn(dto30);

                List<PostPreviewDTO> result = service.getRecentlyViewedPosts(username);

                // Must preserve original IDs order
                assertEquals(List.of(dto10, dto20, dto30), result);
            }
        }

        @Test
        @DisplayName("Should skip null posts when mapping")
        void skipsNullPosts() {
            String username = "eve";
            User user = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(user.getId()).thenReturn(7L);

            List<Long> ids = List.of(100L, 200L);
            when(recentViewService.getRecentPostIds(7L)).thenReturn(ids);

            Post p100 = mock(Post.class); when(p100.getId()).thenReturn(100L);
            // missing 200L post from DB
            when(postRepository.findAllById(ids)).thenReturn(List.of(p100));

            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of());

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostPreviewDTO dto100 = mock(PostPreviewDTO.class);
                mocked.when(() -> PostMapper.toPreviewDTO(p100, false)).thenReturn(dto100);

                List<PostPreviewDTO> result = service.getRecentlyViewedPosts(username);

                assertEquals(List.of(dto100), result);
            }
        }
    }

    @Nested
    @DisplayName("getPreviewPostsByIds")
    class GetPreviewPostsByIds {

        @Test
        @DisplayName("Should return empty list when ids is null")
        void idsNull_returnsEmptyList() {
            List<PostPreviewDTO> result = service.getPreviewPostsByIds(null, "any");
            assertTrue(result.isEmpty());
            verifyNoInteractions(postRepository, hiddenPostService);
        }

        @Test
        @DisplayName("Should return empty list when ids is empty")
        void idsEmpty_returnsEmptyList() {
            List<PostPreviewDTO> result = service.getPreviewPostsByIds(List.of(), "any");
            assertTrue(result.isEmpty());
            verifyNoInteractions(postRepository, hiddenPostService);
        }

        @Test
        @DisplayName("Should map posts by given ids order and apply hidden flags")
        void mapsByIdsOrder_withHiddenFlags() {
            List<Long> ids = List.of(5L, 6L);
            String username = "charlie";

            Post p5 = mock(Post.class); when(p5.getId()).thenReturn(5L);
            Post p6 = mock(Post.class); when(p6.getId()).thenReturn(6L);

            when(postRepository.findAllById(ids)).thenReturn(List.of(p6, p5));
            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of(6L));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostPreviewDTO dto5 = mock(PostPreviewDTO.class);
                PostPreviewDTO dto6 = mock(PostPreviewDTO.class);

                mocked.when(() -> PostMapper.toPreviewDTO(p5, false)).thenReturn(dto5);
                mocked.when(() -> PostMapper.toPreviewDTO(p6, true)).thenReturn(dto6);

                List<PostPreviewDTO> result = service.getPreviewPostsByIds(ids, username);

                assertEquals(List.of(dto5, dto6), result);
            }
        }

        @Test
        @DisplayName("Should skip null posts when mapping")
        void skipsNulls_inIds() {
            List<Long> ids = List.of(1L, 2L);
            String username = "dave";

            Post p1 = mock(Post.class); when(p1.getId()).thenReturn(1L);
            // missing id=2
            when(postRepository.findAllById(ids)).thenReturn(List.of(p1));
            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of());

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostPreviewDTO dto1 = mock(PostPreviewDTO.class);
                mocked.when(() -> PostMapper.toPreviewDTO(p1, false)).thenReturn(dto1);

                List<PostPreviewDTO> result = service.getPreviewPostsByIds(ids, username);

                assertEquals(List.of(dto1), result);
            }
        }
    }
}
