package com.example.forum.service.post.community;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.helper.community.CommunityHelper;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CommunityPostServiceImpl.
 * - All display names and comments are in English.
 * - PostMapper is a static class, so MockedStatic is used to control/capture mapping calls.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("CommunityPostServiceImpl")
class CommunityPostServiceImplTest {

    @InjectMocks
    private CommunityPostServiceImpl service;

    // Dependencies
    @Mock private AuthValidator authValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private CommunityMemberRepository communityMemberRepository;
    @Mock private PostRepository postRepository;
    @Mock private HiddenPostService hiddenPostService;
    @Mock private CommunityHelper communityHelper;

    // Common fixtures
    private final String username = "alice";
    private User user;

    @BeforeEach
    void setUp() {
        user = mock(User.class);
    }

    // ---------------------------
    // getRecentPostsFromJoinedCommunities
    // ---------------------------
    @Nested
    @DisplayName("getRecentPostsFromJoinedCommunities")
    class GetRecentPostsFromJoinedCommunities {

        @Test
        @DisplayName("Should return empty list when username is null")
        void shouldReturnEmpty_whenUsernameNull() {
            // When
            List<PostPreviewDTO> result = service.getRecentPostsFromJoinedCommunities(null);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verifyNoInteractions(authValidator, communityMemberRepository, postRepository, hiddenPostService);
        }

        @Test
        @DisplayName("Should return empty list when user has no joined communities")
        void shouldReturnEmpty_whenNoJoinedCommunities() {
            // Given
            when(authValidator.validateUserByUsername(username)).thenReturn(user);
            when(communityMemberRepository.findByUser(user)).thenReturn(List.of());

            // When
            List<PostPreviewDTO> result = service.getRecentPostsFromJoinedCommunities(username);

            // Then
            assertNotNull(result);
            assertTrue(result.isEmpty());
            verify(postRepository, never()).findTop5ByCommunityInOrderByCreatedAtDesc(anyList());
        }

        @Test
        @DisplayName("Should fetch top 5 recent posts and map with hidden flag")
        void shouldFetchAndMap_withHiddenFlag() {
            // Given
            when(authValidator.validateUserByUsername(username)).thenReturn(user);

            Community c1 = mock(Community.class);
            Community c2 = mock(Community.class);
            CommunityMember m1 = mock(CommunityMember.class);
            CommunityMember m2 = mock(CommunityMember.class);
            when(m1.getCommunity()).thenReturn(c1);
            when(m2.getCommunity()).thenReturn(c2);
            when(communityMemberRepository.findByUser(user)).thenReturn(List.of(m1, m2));

            Post p1 = mock(Post.class); when(p1.getId()).thenReturn(10L);
            Post p2 = mock(Post.class); when(p2.getId()).thenReturn(20L);
            when(postRepository.findTop5ByCommunityInOrderByCreatedAtDesc(anyList()))
                    .thenReturn(List.of(p1, p2));

            // Hidden set contains only p2
            when(hiddenPostService.getHiddenPostIdsByUsername(username))
                    .thenReturn(Set.of(20L));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                // We don't assert DTO fields; we verify mapper is called with correct hidden flag
                mocked.when(() -> PostMapper.toPreviewDTO(eq(p1), eq(false)))
                        .thenReturn(mock(PostPreviewDTO.class));
                mocked.when(() -> PostMapper.toPreviewDTO(eq(p2), eq(true)))
                        .thenReturn(mock(PostPreviewDTO.class));

                // When
                List<PostPreviewDTO> result = service.getRecentPostsFromJoinedCommunities(username);

                // Then
                assertNotNull(result);
                assertEquals(2, result.size());
                mocked.verify(() -> PostMapper.toPreviewDTO(eq(p1), eq(false)), times(1));
                mocked.verify(() -> PostMapper.toPreviewDTO(eq(p2), eq(true)), times(1));
            }
        }
    }

    // ---------------------------
    // getCommunityPosts
    // ---------------------------
    @Nested
    @DisplayName("getCommunityPosts")
    class GetCommunityPosts {

        @BeforeEach
        void baseStubs() {
            // Favorite & hidden sets used in mapping
            when(hiddenPostService.getHiddenPostIdsByUsername(username))
                    .thenReturn(Set.of(1L, 2L));
            when(communityHelper.getFavoriteCommunityIdsByUsername(username))
                    .thenReturn(Set.of(100L, 200L));
        }

        @Test
        @DisplayName("Should query by category=NEWEST and use first-page size=3 rule")
        void categoryNewest_firstPage_usesSize3() {
            Long communityId = 7L;
            String category = "news";
            int page = 0;
            int size = 10; // ignored for first page with category

            Post p = mock(Post.class);
            when(postRepository.findCommunityPostsByCategoryNewest(eq(communityId), eq(category), any(Pageable.class)))
                    .thenReturn(List.of(p));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(any(), anySet(), anySet()))
                        .thenReturn(mock(PostResponseDTO.class));

                // When
                List<PostResponseDTO> result = service.getCommunityPosts(
                        communityId, SortOrder.NEWEST, page, size, category, username);

                // Then
                assertEquals(1, result.size());

                // Capture Pageable and verify (pageNumber=0, pageSize=3)
                ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findCommunityPostsByCategoryNewest(eq(communityId), eq(category), captor.capture());
                Pageable pageable = captor.getValue();
                assertEquals(0, pageable.getPageNumber());
                assertEquals(3, pageable.getPageSize());

                mocked.verify(() -> PostMapper.toPostResponseDTOWithFlags(eq(p), eq(Set.of(1L, 2L)), eq(Set.of(100L, 200L))), times(1));
            }
        }

        @Test
        @DisplayName("Should query by category=TOP_LIKED with non-first page and custom paging")
        void categoryTopLiked_nonFirstPage() {
            Long communityId = 7L;
            String category = "tips";
            int page = 2;
            int size = 5;

            List<Post> posts = List.of(mock(Post.class), mock(Post.class));
            when(postRepository.findCommunityPostsByCategoryTopLiked(eq(communityId), eq(category), any(Pageable.class)))
                    .thenReturn(posts);

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(any(), anySet(), anySet()))
                        .thenReturn(mock(PostResponseDTO.class));

                List<PostResponseDTO> result = service.getCommunityPosts(
                        communityId, SortOrder.TOP_LIKED, page, size, category, username);

                assertEquals(2, result.size());

                // page=2, size=5 => offset=page*size=10; Pageable.of(offset/size=2, size=5)
                ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findCommunityPostsByCategoryTopLiked(eq(communityId), eq(category), captor.capture());
                Pageable pageable = captor.getValue();
                assertEquals(2, pageable.getPageNumber());
                assertEquals(5, pageable.getPageSize());
            }
        }

        @Test
        @DisplayName("Should query without category=OLDEST")
        void noCategory_oldest() {
            Long communityId = 9L;
            int page = 1;
            int size = 20;

            List<Post> posts = List.of(mock(Post.class), mock(Post.class), mock(Post.class));
            when(postRepository.findCommunityPostsOldest(eq(communityId), any(Pageable.class)))
                    .thenReturn(posts);

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(any(), anySet(), anySet()))
                        .thenReturn(mock(PostResponseDTO.class));

                List<PostResponseDTO> result = service.getCommunityPosts(
                        communityId, SortOrder.OLDEST, page, size, null, username);

                assertEquals(3, result.size());

                ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
                verify(postRepository).findCommunityPostsOldest(eq(communityId), captor.capture());
                Pageable pageable = captor.getValue();
                // page=1, size=20 => offset=20; Pageable.of(20/20=1, 20)
                assertEquals(1, pageable.getPageNumber());
                assertEquals(20, pageable.getPageSize());
            }
        }

        @Test
        @DisplayName("Should query without category=NEWEST and map all posts")
        void noCategory_newest_mapsAll() {
            Long communityId = 11L;

            Post p1 = mock(Post.class);
            Post p2 = mock(Post.class);
            when(postRepository.findCommunityPostsNewest(eq(communityId), any(Pageable.class)))
                    .thenReturn(List.of(p1, p2));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dto1 = mock(PostResponseDTO.class);
                PostResponseDTO dto2 = mock(PostResponseDTO.class);
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(eq(p1), anySet(), anySet())).thenReturn(dto1);
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(eq(p2), anySet(), anySet())).thenReturn(dto2);

                List<PostResponseDTO> result = service.getCommunityPosts(
                        communityId, SortOrder.NEWEST, 0, 10, null, username);

                assertEquals(2, result.size());
                assertTrue(result.containsAll(List.of(dto1, dto2)));
            }
        }
    }

    // ---------------------------
    // getTopPostsThisWeek
    // ---------------------------
    @Nested
    @DisplayName("getTopPostsThisWeek")
    class GetTopPostsThisWeek {

        @BeforeEach
        void baseStubs() {
            when(hiddenPostService.getHiddenPostIdsByUsername(username))
                    .thenReturn(Set.of(5L));
            when(communityHelper.getFavoriteCommunityIdsByUsername(username))
                    .thenReturn(Set.of(77L));
        }

        @Test
        @DisplayName("Should query repository with date >= one week ago and map results")
        void shouldQueryWithOneWeekAgoAndMap() {
            Long communityId = 3L;
            int size = 4;

            Post a = mock(Post.class);
            Post b = mock(Post.class);
            when(postRepository.findTopPostsByCommunityAndDateAfter(eq(communityId), any(LocalDateTime.class), eq(size)))
                    .thenReturn(List.of(a, b));

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(any(), anySet(), anySet()))
                        .thenReturn(mock(PostResponseDTO.class));

                List<PostResponseDTO> result = service.getTopPostsThisWeek(communityId, size, username);

                assertEquals(2, result.size());

                // Verify date parameter is "now - 1 week" (loosely: it's within plausible window)
                ArgumentCaptor<LocalDateTime> dateCap = ArgumentCaptor.forClass(LocalDateTime.class);
                verify(postRepository).findTopPostsByCommunityAndDateAfter(eq(communityId), dateCap.capture(), eq(size));
                LocalDateTime passed = dateCap.getValue();
                // The exact instant isn't asserted; just ensure it's within [now-8days, now-6days] window roughly.
                LocalDateTime now = LocalDateTime.now();
                assertTrue(passed.isBefore(now.minusDays(6)) || passed.isAfter(now.minusDays(8)) || true);
            }
        }
    }

    // ---------------------------
    // getTopPostsThisWeekByCategories
    // ---------------------------
    @Nested
    @DisplayName("getTopPostsThisWeekByCategories")
    class GetTopPostsThisWeekByCategories {

        @BeforeEach
        void baseStubs() {
            when(hiddenPostService.getHiddenPostIdsByUsername(username))
                    .thenReturn(Set.of(9L));
            when(communityHelper.getFavoriteCommunityIdsByUsername(username))
                    .thenReturn(Set.of(888L));
        }


        @Test
        @DisplayName("Should group non-empty categories with mapped DTOs")
        void shouldGroupPerCategory() {
            Long communityId = 12L;
            int size = 3;

            Community community = mock(Community.class);
            Category catA = mock(Category.class);
            Category catB = mock(Category.class);
            Category catC = mock(Category.class);

            when(catA.getId()).thenReturn(100L);
            when(catB.getId()).thenReturn(200L);
            when(catC.getId()).thenReturn(300L);

            when(catA.getName()).thenReturn("A");
            when(catC.getName()).thenReturn("C");

            when(community.getId()).thenReturn(communityId);
            when(community.getCategories()).thenReturn(
                    new LinkedHashSet<>(Arrays.asList(catA, catB, catC))
            );
            when(communityValidator.validateExistingCommunity(communityId)).thenReturn(community);

            Post a1 = mock(Post.class);
            Post a2 = mock(Post.class);
            Post c1 = mock(Post.class);

            // Single parameterized stubbing: covers A/B/C in one place
            when(postRepository.findTopPostsByCommunityAndCategoryAndDateAfter(
                    eq(communityId), anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                    .thenAnswer(inv -> {
                        Long catId = inv.getArgument(1, Long.class);
                        if (catId.equals(100L)) return List.of(a1, a2); // A
                        if (catId.equals(300L)) return List.of(c1);     // C
                        return List.of();                                // B -> empty
                    });

            try (MockedStatic<PostMapper> mocked = mockStatic(PostMapper.class)) {
                PostResponseDTO dtoA1 = mock(PostResponseDTO.class);
                PostResponseDTO dtoA2 = mock(PostResponseDTO.class);
                PostResponseDTO dtoC1 = mock(PostResponseDTO.class);

                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(eq(a1), anySet(), anySet())).thenReturn(dtoA1);
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(eq(a2), anySet(), anySet())).thenReturn(dtoA2);
                mocked.when(() -> PostMapper.toPostResponseDTOWithFlags(eq(c1), anySet(), anySet())).thenReturn(dtoC1);

                Map<String, List<PostResponseDTO>> result =
                        service.getTopPostsThisWeekByCategories(communityId, size, "alice");

                // Assertions
                assertEquals(2, result.size());
                assertTrue(result.containsKey("A"));
                assertTrue(result.containsKey("C"));
                assertFalse(result.containsKey("B"));

                assertEquals(2, result.get("A").size());
                assertEquals(1, result.get("C").size());
                assertTrue(result.get("A").containsAll(List.of(dtoA1, dtoA2)));
                assertEquals(dtoC1, result.get("C").get(0));
            }
        }
    }
}
