package com.example.forum.service.post.profile;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
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

@Service
@RequiredArgsConstructor
public class ProfilePostServiceImpl implements ProfilePostService {

    private final AuthValidator authValidator;
    private final PostRepository postRepository;
    private final HiddenPostRepository hiddenPostRepository;

    @Override
    public List<PostResponseDTO> getProfilePosts(String targetUsername, String currentUsername, SortOrder sort, int page, int size) {

        User target = authValidator.validateUserByUsername(targetUsername);
        User current = authValidator.validateUserByUsername(currentUsername);

        boolean includePrivate = target.getId().equals(current.getId());

        Set<Long> hiddenPostIds = new HashSet<>(hiddenPostRepository.findHiddenPostIdsByUser(current));

        Pageable pageable = getSortedPageable(sort, page, size);

        Page<Post> postPage = switch (sort) {
            case TOP_LIKED -> postRepository.findPostsByAuthorWithLikeCount(target, includePrivate, pageable);
            default -> postRepository.findPostsByAuthor(target, includePrivate, pageable);
        };

        return postPage.stream()
                .map(post -> PostMapper.toPostResponseDTO(post, hiddenPostIds.contains(post.getId())))
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
