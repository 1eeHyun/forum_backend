package com.example.forum.service.post.community;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.helper.community.CommunityHelper;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityPostServiceImpl implements CommunityPostService {

    // Validators
    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;

    // Repositories
    private final CommunityMemberRepository communityMemberRepository;
    private final CommunityFavoriteRepository communityFavoriteRepository;
    private final PostRepository postRepository;

    // Services
    private final HiddenPostService hiddenPostService;

    // Helper
    private final CommunityHelper communityHelper;

    @Override
    public List<PostPreviewDTO> getRecentPostsFromJoinedCommunities(String username) {

        if (username == null)
            return List.of();

        User user = authValidator.validateUserByUsername(username);
        List<CommunityMember> memberShips = communityMemberRepository.findByUser(user);
        List<Community> joinedCommunities = memberShips.stream()
                .map(CommunityMember::getCommunity)
                .toList();

        if (joinedCommunities.isEmpty()) return List.of();

        List<Post> posts = postRepository.findTop5ByCommunityInOrderByCreatedAtDesc(joinedCommunities);
        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);

        return posts.stream()
                .map(post -> PostMapper.toPreviewDTO(post, hiddenPostIds.contains(post.getId())))
                .toList();
    }

    @Override
    public List<PostResponseDTO> getCommunityPosts(Long communityId, SortOrder sort, int page, int size, String category, String username) {

        int offset = (category != null && page == 0) ? 0 : page * size;
        int limit = (category != null && page == 0) ? 3 : size;

        Pageable pageable = PageRequest.of(offset / limit, limit);

        List<Post> posts;

        if (category != null) {
            posts = switch (sort) {
                case NEWEST -> postRepository.findCommunityPostsByCategoryNewest(communityId, category, pageable);
                case OLDEST -> postRepository.findCommunityPostsByCategoryOldest(communityId, category, pageable);
                case TOP_LIKED -> postRepository.findCommunityPostsByCategoryTopLiked(communityId, category, pageable);
            };
        } else {
            posts = switch (sort) {
                case NEWEST -> postRepository.findCommunityPostsNewest(communityId, pageable);
                case OLDEST -> postRepository.findCommunityPostsOldest(communityId, pageable);
                case TOP_LIKED -> postRepository.findCommunityPostsTopLiked(communityId, pageable);
            };
        }

        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);
        Set<Long> favoriteCommunityIds = communityHelper.getFavoriteCommunityIdsByUsername(username);

        return posts.stream()
                .map(post -> PostMapper.toPostResponseDTOWithFlags(post, hiddenPostIds, favoriteCommunityIds))
                .toList();
    }

    @Override
    public List<PostResponseDTO> getTopPostsThisWeek(Long communityId, int size, String username) {

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        List<Post> topPosts = postRepository.findTopPostsByCommunityAndDateAfter(
                communityId, oneWeekAgo, size
        );

        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);
        Set<Long> favoriteCommunityIds = communityHelper.getFavoriteCommunityIdsByUsername(username);

        return topPosts.stream()
                .map(post -> PostMapper.toPostResponseDTOWithFlags(post, hiddenPostIds, favoriteCommunityIds))
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<PostResponseDTO>> getTopPostsThisWeekByCategories(Long communityId, int size, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        LocalDateTime fromDate = LocalDateTime.now().minusWeeks(1);
        Set<Category> categories = community.getCategories();

        Map<String, List<PostResponseDTO>> result = new HashMap<>();
        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);
        Set<Long> favoriteCommunityIds = communityHelper.getFavoriteCommunityIdsByUsername(username);

        for (Category category : categories) {

            List<Post> posts = postRepository.findTopPostsByCommunityAndCategoryAndDateAfter(
                    community.getId(), category.getId(), fromDate, PageRequest.of(0, size)
            );

            List<PostResponseDTO> dtoList = posts.stream()
                    .map(post -> PostMapper.toPostResponseDTOWithFlags(post, hiddenPostIds, favoriteCommunityIds))
                    .toList();

            if (!dtoList.isEmpty()) {
                result.put(category.getName(), dtoList);
            }
        }

        return result;
    }
}
