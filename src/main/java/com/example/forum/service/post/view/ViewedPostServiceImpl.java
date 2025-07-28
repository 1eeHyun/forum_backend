package com.example.forum.service.post.view;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ViewedPostServiceImpl implements ViewedPostService {

    private final AuthValidator authValidator;
    private final RecentViewService recentViewService;
    private final PostRepository postRepository;
    private final HiddenPostService hiddenPostService;

    @Override
    public List<PostPreviewDTO> getRecentlyViewedPosts(String username) {

        User user = authValidator.validateUserByUsername(username);
        Long userId = user.getId();

        // Retrieve postId list from Redis post
        List<Long> ids = recentViewService.getRecentPostIds(userId);
        if (ids.isEmpty()) {
            return Collections.emptyList();
        }

        // Retrieve posts from DB
        List<Post> posts = postRepository.findAllById(ids);

        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);

        // postId → Post Mapping
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        // Convert to DTO
        return ids.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(post -> PostMapper.toPreviewDTO(post, hiddenPostIds.contains(post.getId())))
                .toList();
    }

    @Override
    public List<PostPreviewDTO> getPreviewPostsByIds(List<Long> ids, String username) {

        if (ids == null || ids.isEmpty())
            return Collections.emptyList();

        List<Post> posts = postRepository.findAllById(ids);
        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);

        Map<Long, Post> map = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(post -> PostMapper.toPreviewDTO(post, hiddenPostIds.contains(post.getId())))
                .toList();
    }
}
