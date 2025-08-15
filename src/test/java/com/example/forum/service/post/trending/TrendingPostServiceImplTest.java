package com.example.forum.service.post.trending;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for TrendingPostServiceImpl.
 * - Display names and comments are in English.
 * - Verifies hidden filtering, favorite flags, paging and mapper usage.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("TrendingPostServiceImpl")
class TrendingPostServiceImplTest {

    @InjectMocks
    private TrendingPostServiceImpl service;

    @Mock private PostRepository postRepository;
    @Mock private HiddenPostService hiddenPostService;
    @Mock private CommunityFavoriteRepository communityFavoriteRepository;
    @Mock private AuthValidator authValidator;

    @Nested
    @DisplayName("getTrendingPosts")
    class GetTrendingPosts {

        @Test
        @DisplayName("Should return mapped posts with no hidden/favorite when username is null")
        void noUsername_returnsPosts_noHiddenNoFavorite() {
            // Given: repository returns two posts
            Post p1 = mock(Post.class); when(p1.getId()).thenReturn(1L);
            Post p2 = mock(Post.class); when(p2.getId()).thenReturn(2L);

            // p1 has no category (no community), p2 has a community but username is null => no favorites
            Category cat2 = mock(Category.class);
            Community community2 = mock(Community.class);
            when(cat2.getCommunity()).thenReturn(community2);
            when(p2.getCategory()).thenReturn(cat2);
            when(p1.getCategory()).thenReturn(null);

            when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(p1, p2)));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dto1 = mock(PostResponseDTO.class);
                PostResponseDTO dto2 = mock(PostResponseDTO.class);

                // Expect hidden=false and favorite=false for both (username null => empty sets)
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(p1), eq(false), eq(false))).thenReturn(dto1);
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(p2), eq(false), eq(false))).thenReturn(dto2);

                // When
                List<PostResponseDTO> result = service.getTrendingPosts(null);

                // Then
                assertEquals(List.of(dto1, dto2), result);

                // Verify paging args (page 0, size 20)
                ArgumentCaptor<Pageable> pageCap = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findTrendingPosts(any(LocalDateTime.class), pageCap.capture());
                Pageable pageable = pageCap.getValue();
                assertEquals(0, pageable.getPageNumber());
                assertEquals(20, pageable.getPageSize());

                // With username=null, these collaborators must not be used
                verifyNoInteractions(authValidator, communityFavoriteRepository, hiddenPostService);
            }
        }

        @Test
        @DisplayName("Should filter out hidden posts and mark favorites when username provided")
        void withUsername_filtersHidden_andMarksFavorite() {
            String username = "alice";

            // Posts: pHidden (id=10) should be filtered out; pFav (id=20) is favorite; pPlain (id=30) normal
            Post pHidden = mock(Post.class); when(pHidden.getId()).thenReturn(10L);
            Post pFav = mock(Post.class);   when(pFav.getId()).thenReturn(20L);
            Post pPlain = mock(Post.class); when(pPlain.getId()).thenReturn(30L);

            // pFav has category -> community id 777 (favorite)
            Category catFav = mock(Category.class);
            Community favCommunity = mock(Community.class);
            when(favCommunity.getId()).thenReturn(777L);
            when(catFav.getCommunity()).thenReturn(favCommunity);
            when(pFav.getCategory()).thenReturn(catFav);

            // pPlain has no category
            when(pPlain.getCategory()).thenReturn(null);

            when(postRepository.findTrendingPosts(any(LocalDateTime.class), any(Pageable.class)))
                    .thenReturn(new PageImpl<>(List.of(pHidden, pFav, pPlain)));

            // Hidden ids include 10 => pHidden filtered
            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of(10L));

            // Favorites for user
            User alice = mock(User.class);
            when(authValidator.validateUserByUsername(username)).thenReturn(alice);

            var favEntity = mock(com.example.forum.model.community.CommunityFavorite.class);
            when(favEntity.getCommunity()).thenReturn(favCommunity);
            when(communityFavoriteRepository.findAllByUser(alice)).thenReturn(List.of(favEntity));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dtoFav = mock(PostResponseDTO.class);
                PostResponseDTO dtoPlain = mock(PostResponseDTO.class);

                // pFav => hidden=false, favorite=true
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(pFav), eq(false), eq(true))).thenReturn(dtoFav);
                // pPlain => hidden=false, favorite=false
                mocked.when(() -> PostMapper.toPostResponseDTO(eq(pPlain), eq(false), eq(false))).thenReturn(dtoPlain);

                // When
                List<PostResponseDTO> result = service.getTrendingPosts(username);

                // Then: pHidden filtered out, order preserved for remaining
                assertEquals(List.of(dtoFav, dtoPlain), result);

                // Verify collaborator calls
                verify(hiddenPostService, times(1)).getHiddenPostIdsByUsername(username);
                verify(authValidator, times(1)).validateUserByUsername(username);
                verify(communityFavoriteRepository, times(1)).findAllByUser(alice);
            }
        }
    }

    @Nested
    @DisplayName("getTopPostsThisWeek")
    class GetTopPostsThisWeek {

        @Test
        @DisplayName("Should map posts to previews with hidden flag based on HiddenPostService")
        void mapsPreviewWithHiddenFlag() {
            String username = "bob";

            // Given: repository returns three posts
            Post a = mock(Post.class); when(a.getId()).thenReturn(1L);
            Post b = mock(Post.class); when(b.getId()).thenReturn(2L);
            Post c = mock(Post.class); when(c.getId()).thenReturn(3L);

            when(postRepository.findTopPostsSince(any(LocalDateTime.class), eq(PageRequest.of(0, 5))))
                    .thenReturn(List.of(a, b, c));

            // Hidden contains only id=2
            when(hiddenPostService.getHiddenPostIdsByUsername(username)).thenReturn(Set.of(2L));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostPreviewDTO dA = mock(PostPreviewDTO.class);
                PostPreviewDTO dB = mock(PostPreviewDTO.class);
                PostPreviewDTO dC = mock(PostPreviewDTO.class);

                mocked.when(() -> PostMapper.toPreviewDTO(eq(a), eq(false))).thenReturn(dA);
                mocked.when(() -> PostMapper.toPreviewDTO(eq(b), eq(true))).thenReturn(dB);
                mocked.when(() -> PostMapper.toPreviewDTO(eq(c), eq(false))).thenReturn(dC);

                // When
                List<PostPreviewDTO> result = service.getTopPostsThisWeek(username);

                // Then
                assertEquals(List.of(dA, dB, dC), result);

                // Verify args: oneWeekAgo-ish and paging (0,5)
                verify(postRepository, times(1))
                        .findTopPostsSince(any(LocalDateTime.class), eq(PageRequest.of(0, 5)));

                // Hidden service called even if username might be null in other cases.
                verify(hiddenPostService, times(1)).getHiddenPostIdsByUsername(username);
            }
        }
    }
}
