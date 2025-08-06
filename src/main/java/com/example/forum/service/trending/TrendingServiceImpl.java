package com.example.forum.service.trending;

import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.trend.TrendingSidebarDTO;
import com.example.forum.helper.community.CommunityHelper;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.post.hidden.HiddenPostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class TrendingServiceImpl implements TrendingService {

    private final PostRepository postRepository;
    private final HiddenPostService hiddenPostService;

    private final CommunityRepository communityRepository;

    private final CommunityHelper communityHelper;

    @Override
    public List<PostResponseDTO> getTrendingPosts(String username) {

        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        List<Post> posts = postRepository.findTrendingPosts(from, PageRequest.of(0, 20)).getContent();

        Set<Long> hiddenPostIds = username != null
                ? hiddenPostService.getHiddenPostIdsByUsername(username)
                : Collections.emptySet();

        Set<Long> favoriteCommunityIds = username != null
                ? communityHelper.getFavoriteCommunityIdsByUsername(username)
                : Collections.emptySet();


        return posts.stream()
                .filter(post -> !hiddenPostIds.contains(post.getId()))
                .map(post -> PostMapper.toPostResponseDTOWithFlags(post, hiddenPostIds, favoriteCommunityIds))
                .toList();
    }

    @Override
    public TrendingSidebarDTO getTrendingSidebarData(String username) {

        LocalDateTime from = LocalDateTime.now().minusMonths(1);
        List<Community> topCommunities = communityRepository.findTrendingCommunities(from, PageRequest.of(0, 5));

        Set<Long> favoriteCommunityIds = communityHelper.getFavoriteCommunityIdsByUsername(username);

        return TrendingSidebarDTO.builder()
                .trendingCommunities(
                        topCommunities.stream()
                                .map(community -> CommunityMapper.toPreviewDTO(
                                        community,
                                        favoriteCommunityIds.contains(community.getId())
                                ))
                                .toList()
                )
                .build();
    }
}
