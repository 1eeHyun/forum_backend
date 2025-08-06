package com.example.forum.service.post.profile;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.post.HiddenPostRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfilePostServiceImpl implements ProfilePostService {

    private final AuthValidator authValidator;
    private final PostRepository postRepository;
    private final HiddenPostRepository hiddenPostRepository;
    private final CommunityFavoriteRepository communityFavoriteRepository;

    @Override
    public List<PostResponseDTO> getProfilePosts(String targetUsername, String currentUsername, SortOrder sort, int page, int size) {

        User target = authValidator.validateUserByUsername(targetUsername);
        User current = authValidator.validateUserByUsername(currentUsername);

        boolean includePrivate = target.getId().equals(current.getId());

        Set<Long> hiddenPostIds = new HashSet<>(hiddenPostRepository.findHiddenPostIdsByUser(current));

        Set<Long> favoriteCommunityIds = communityFavoriteRepository.findAllByUser(current).stream()
                .map(fav -> fav.getCommunity().getId())
                .collect(Collectors.toSet());

        Pageable pageable = getSortedPageable(sort, page, size);

        Page<Post> postPage = switch (sort) {
            case TOP_LIKED -> postRepository.findPostsByAuthorWithLikeCount(target, includePrivate, pageable);
            default -> postRepository.findPostsByAuthor(target, includePrivate, pageable);
        };

        return postPage.stream()
                .map(post -> {
                    boolean isHidden = hiddenPostIds.contains(post.getId());

                    Community community = post.getCategory() != null
                            ? post.getCategory().getCommunity()
                            : null;

                    boolean isFavorite = community != null && favoriteCommunityIds.contains(community.getId());

                    return PostMapper.toPostResponseDTO(post, isHidden, isFavorite);
                })
                .toList();
    }

    private Pageable getSortedPageable(SortOrder sort, int page, int size) {
        return switch (sort) {
            case NEWEST -> PageRequest.of(page, size, Sort.by("createdAt").descending());
            case OLDEST -> PageRequest.of(page, size, Sort.by("createdAt").ascending());
            case TOP_LIKED -> PageRequest.of(page, size);
        };
    }
}
