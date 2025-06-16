package com.example.forum.service.post.community;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CommunityPostServiceImplTest {

    @Mock private AuthValidator authValidator;
    @Mock private CommunityValidator communityValidator;
    @Mock private CommunityMemberRepository communityMemberRepository;
    @Mock private PostRepository postRepository;

    @InjectMocks
    private CommunityPostServiceImpl communityPostService;

    private User user;
    private Community community;
    private Category category;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        Profile profile = Profile.builder()
                .nickname("John")
                .imageUrl("test.jpg")
                .imagePositionX(0.5)
                .imagePositionY(0.5)
                .build();

        user = User.builder()
                .id(1L)
                .username("john")
                .email("john@example.com")
                .password("pw")
                .profile(profile)
                .build();

        community = Community.builder()
                .id(1L)
                .name("Test Community")
                .categories(Set.of())
                .build();

        category = Category.builder()
                .id(1L)
                .name("general")
                .community(community)
                .build();

        post = Post.builder()
                .id(1L)
                .title("Test Title")
                .content("Test Content")
                .author(user)
                .category(category)
                .visibility(Visibility.PUBLIC)
                .likes(new ArrayList<>())
                .comments(new ArrayList<>())
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Nested
    @DisplayName("getRecentPostsFromJoinedCommunities()")
    class GetRecentPosts {

        @Test
        @DisplayName("Success: Joined communities exist")
        void success_withJoinedCommunities() {
            CommunityMember member = new CommunityMember(community, user, CommunityRole.MEMBER);

            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(communityMemberRepository.findByUser(user)).thenReturn(List.of(member));
            when(postRepository.findTop5ByCommunityInOrderByCreatedAtDesc(List.of(community)))
                    .thenReturn(List.of(post));

            List<PostPreviewDTO> result = communityPostService.getRecentPostsFromJoinedCommunities("john");

            assertThat(result).isNotNull();
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Success: No joined communities")
        void success_noJoinedCommunities() {
            when(authValidator.validateUserByUsername("john")).thenReturn(user);
            when(communityMemberRepository.findByUser(user)).thenReturn(List.of());

            List<PostPreviewDTO> result = communityPostService.getRecentPostsFromJoinedCommunities("john");

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Success: Null username returns null")
        void success_nullUsername() {
            List<PostPreviewDTO> result = communityPostService.getRecentPostsFromJoinedCommunities(null);
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("getTopPostsThisWeekByCategories()")
    class TopPostsByCategory {

        @Test
        @DisplayName("Success: Grouped by categories")
        void success_groupedByCategory() {
            community.setCategories(Set.of(category));

            when(communityValidator.validateExistingCommunity(1L)).thenReturn(community);
            when(postRepository.findTopPostsByCommunityAndCategoryAndDateAfter(
                    eq(1L), eq(1L), any(), any())).thenReturn(List.of(post));

            Map<String, List<PostResponseDTO>> result = communityPostService.getTopPostsThisWeekByCategories(1L, 3);

            assertThat(result).containsKey("general");
            assertThat(result.get("general")).hasSize(1);
        }

        @Test
        @DisplayName("Success: No posts returns empty map")
        void success_emptyCategoryResults() {
            community.setCategories(Set.of(category));

            when(communityValidator.validateExistingCommunity(1L)).thenReturn(community);
            when(postRepository.findTopPostsByCommunityAndCategoryAndDateAfter(
                    eq(1L), eq(1L), any(), any())).thenReturn(List.of());

            Map<String, List<PostResponseDTO>> result = communityPostService.getTopPostsThisWeekByCategories(1L, 3);

            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("Failure: Invalid community throws exception")
        void fail_invalidCommunity() {
            when(communityValidator.validateExistingCommunity(999L))
                    .thenThrow(new IllegalArgumentException("Not Found"));

            assertThatThrownBy(() -> communityPostService.getTopPostsThisWeekByCategories(999L, 3))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("getTopPostsThisWeek()")
    class TopPosts {

        @Test
        @DisplayName("Success: Return top posts this week")
        void success_topPosts() {
            when(postRepository.findTopPostsByCommunityAndDateAfter(eq(1L), any(), eq(5)))
                    .thenReturn(List.of(post));

            List<PostResponseDTO> result = communityPostService.getTopPostsThisWeek(1L, 5);

            assertThat(result).hasSize(1);
        }
    }

    @Nested
    @DisplayName("getCommunityPosts()")
    class CommunityPosts {

        @Test
        @DisplayName("Success: Newest posts with category")
        void success_newestWithCategory() {
            when(postRepository.findCommunityPostsByCategoryNewest(eq(1L), eq("general"), any()))
                    .thenReturn(List.of(post));

            List<PostResponseDTO> result = communityPostService.getCommunityPosts(
                    1L, SortOrder.NEWEST, 0, 10, "general"
            );

            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("Success: Top liked posts without category")
        void success_topLikedNoCategory() {
            when(postRepository.findCommunityPostsTopLiked(eq(1L), any()))
                    .thenReturn(List.of(post));

            List<PostResponseDTO> result = communityPostService.getCommunityPosts(
                    1L, SortOrder.TOP_LIKED, 1, 10, null
            );

            assertThat(result).hasSize(1);
        }
    }
}