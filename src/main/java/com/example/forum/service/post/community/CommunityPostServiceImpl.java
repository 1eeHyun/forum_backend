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
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.post.PostRepository;
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
    private final PostRepository postRepository;

    @Override
    public List<PostPreviewDTO> getRecentPostsFromJoinedCommunities(String username) {

        if (username == null)
            return null;

        User user = authValidator.validateUserByUsername(username);
        List<CommunityMember> memberShips = communityMemberRepository.findByUser(user);
        List<Community> joinedCommunities = memberShips.stream()
                .map(CommunityMember::getCommunity)
                .toList();

        if (joinedCommunities.isEmpty()) return List.of();

        List<Post> posts = postRepository.findTop5ByCommunityInOrderByCreatedAtDesc(joinedCommunities);

        return posts.stream()
                .map(PostMapper::toPreviewDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO> getCommunityPosts(Long communityId, SortOrder sort, int page, int size, String category) {

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

        return posts.stream()
                .map(PostMapper::toPostResponseDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO> getTopPostsThisWeek(Long communityId, int size) {

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        List<Post> topPosts = postRepository.findTopPostsByCommunityAndDateAfter(
                communityId, oneWeekAgo, size
        );

        return topPosts.stream()
                .map(PostMapper::toPostResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Map<String, List<PostResponseDTO>> getTopPostsThisWeekByCategories(Long communityId, int size) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        LocalDateTime fromDate = LocalDateTime.now().minusWeeks(1);
        Set<Category> categories = community.getCategories();

        Map<String, List<PostResponseDTO>> result = new HashMap<>();

        for (Category category : categories) {

            List<Post> posts = postRepository.findTopPostsByCommunityAndCategoryAndDateAfter(
                    community.getId(), category.getId(), fromDate, PageRequest.of(0, size)
            );

            List<PostResponseDTO> dtoList = posts.stream()
                    .map(PostMapper::toPostResponseDTO)
                    .toList();

            if (!dtoList.isEmpty()) {
                result.put(category.getName(), dtoList);
            }
        }

        return result;
    }
}
