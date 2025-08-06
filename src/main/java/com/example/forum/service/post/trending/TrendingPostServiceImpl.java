package com.example.forum.service.post.trending;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TrendingPostServiceImpl implements TrendingPostService {

    private final PostRepository postRepository;
    private final HiddenPostService hiddenPostService;
    private final CommunityFavoriteRepository communityFavoriteRepository;
    private final AuthValidator authValidator;

    @Override
    public List<PostResponseDTO> getTrendingPosts(String username) {

        LocalDateTime from = LocalDateTime.now().minusDays(1);
        List<Post> posts = postRepository.findTrendingPosts(from, PageRequest.of(0, 20)).getContent();

        Set<Long> hiddenPostIds = username != null
                ? hiddenPostService.getHiddenPostIdsByUsername(username)
                : Collections.emptySet();

        Set<Long> favoriteCommunityIds;
        if (username != null) {
            User user = authValidator.validateUserByUsername(username);
            favoriteCommunityIds = communityFavoriteRepository.findAllByUser(user).stream()
                    .map(fav -> fav.getCommunity().getId())
                    .collect(Collectors.toSet());
        } else {
            favoriteCommunityIds = Set.of(); // or Collections.emptySet()
        }

        return posts.stream()
                .filter(post -> !hiddenPostIds.contains(post.getId()))
                .map(post -> {
                    Community community = post.getCategory() != null
                            ? post.getCategory().getCommunity()
                            : null;
                    boolean isFavorite = (community != null) && favoriteCommunityIds.contains(community.getId());
                    return PostMapper.toPostResponseDTO(post, false, isFavorite);
                })
                .toList();
    }

    @Override
    public List<PostPreviewDTO> getTopPostsThisWeek(String username) {

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        List<Post> posts = postRepository.findTopPostsSince(oneWeekAgo, PageRequest.of(0, 5));
        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);

        return posts.stream()
                .map(post -> PostMapper.toPreviewDTO(post, hiddenPostIds.contains(post.getId())))
                .collect(Collectors.toList());
    }
}
