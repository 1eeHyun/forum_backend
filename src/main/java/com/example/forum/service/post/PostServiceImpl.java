package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostImage;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.common.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CategoryValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    // Validators
    private final AuthValidator authValidator;
    private final PostValidator postValidator;
    private final CategoryValidator categoryValidator;

    // Repositories
    private final PostRepository postRepository;
    private final CommunityMemberRepository communityMemberRepository;

    // Services
    private final S3Service s3Service;
    private final RecentViewService recentViewService;

    @Override
    public List<PostResponseDTO> getPagedPosts(SortOrder sort, int page, int size) {

        int offset = (page == 0) ? 0 : 3 + (page - 1) * 10;
        int limit = (page == 0) ? 3 : 10;

        List<Post> posts = switch (sort) {
            case NEWEST -> postRepository.findPagedPostsNewest(limit, offset);
            case OLDEST -> postRepository.findPagedPostsOldest(limit, offset);
            case TOP_LIKED -> postRepository.findPagedPostsTopLiked(limit, offset);
        };

        return posts.stream()
                .map(PostMapper::toPostResponseDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO> getProfilePosts(String targetUsername, String currentUsername, SortOrder sort, int page, int size) {

        User target = authValidator.validateUserByUsername(targetUsername);
        User current = authValidator.validateUserByUsername(currentUsername);

        boolean includePrivate = target.getId().equals(current.getId());

        Pageable pageable = switch (sort) {
            case NEWEST -> PageRequest.of(page, size, Sort.by("createdAt").descending().and(Sort.by("id").descending()));
            case OLDEST -> PageRequest.of(page, size, Sort.by("createdAt").ascending().and(Sort.by("id").ascending()));
            case TOP_LIKED -> PageRequest.of(page, size, Sort.by(Sort.Order.desc("likeCount"), Sort.Order.desc("createdAt")));
        };

        Page<Post> postPage = switch (sort) {
            case TOP_LIKED -> postRepository.findPostsByAuthorWithLikeCount(target, includePrivate, pageable);
            default -> postRepository.findPostsByAuthor(target, includePrivate, pageable);
        };

        return postPage.stream()
                .map(PostMapper::toPostResponseDTO)
                .toList();
    }

    @Override
    public PostDetailDTO getPostDetail(Long postId, String username) {

        User viewer = null;
        if (username != null)
            viewer = authValidator.validateUserByUsername(username);

        Post post = postValidator.validateDetailPostId(postId);

        if (viewer != null)
            recentViewService.addPostView(viewer.getId(), postId);

        return PostMapper.toPostDetailDTO(post, viewer);
    }

    @Override
    @Transactional
    public PostResponseDTO createPost(PostRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);
        Category category = getValidCategoryIfNeeded(dto);
        postValidator.validatePostCount(dto.getImageUrls());

        Post post = buildPostFromDto(dto, user, category);
        savePostImages(post, dto.getImageUrls());

        Post savedPost = postRepository.save(post);
        return PostMapper.toPostResponseDTO(savedPost);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username) {

        // 1. Get user and post
        User user = authValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        postValidator.validatePostAuthor(post, user);

        // 2. Validate image count and community (optional)
        postValidator.validatePostCount(dto.getImageUrls());
        Category category = getValidCategoryIfNeeded(dto);

        // 3. Delete old images from S3
        List<String> oldImageUrls = post.getImages() == null ? List.of()
                : post.getImages().stream().map(PostImage::getImageUrl).toList();
        s3Service.deleteFiles(oldImageUrls);

        // 4. Update fields
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setVisibility(dto.getVisibility());
        post.setCategory(category);

        // 5. Update images safely
        if (post.getImages() != null) post.getImages().clear();
        if (dto.getImageUrls() != null) {
            for (String url : dto.getImageUrls()) {
                PostImage image = PostImage.builder()
                        .imageUrl(url)
                        .post(post)
                        .build();
                post.getImages().add(image);
            }
        }

        // 6. Optional: explicit save (if needed by repository listeners)
        Post saved = postRepository.save(post); // make sure Hibernate flushes

        // 7. Return response
        return PostMapper.toPostResponseDTO(saved); // or just `post`
    }

    @Override
    @Transactional
    public void deletePost(Long postId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        postValidator.validatePostAuthor(post, user);

        postRepository.delete(post);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return s3Service.upload(file);
    }

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
    public List<PostPreviewDTO> getTopPostsThisWeek() {

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);
        List<Post> posts = postRepository.findTopPostsSince(oneWeekAgo, PageRequest.of(0, 5));

        return posts.stream()
                .map(PostMapper::toPreviewDTO)
                .collect(Collectors.toList());
    }

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

        // postId → Post Mapping
        Map<Long, Post> postMap = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        // Convert to DTO
        return ids.stream()
                .map(postMap::get)
                .filter(Objects::nonNull)
                .map(PostMapper::toPreviewDTO)
                .toList();
    }

    @Override
    public List<PostPreviewDTO> getPreviewPostsByIds(List<Long> ids) {

        if (ids == null || ids.isEmpty())
            return Collections.emptyList();

        List<Post> posts = postRepository.findAllById(ids);

        Map<Long, Post> map = posts.stream()
                .collect(Collectors.toMap(Post::getId, p -> p));

        return ids.stream()
                .map(map::get)
                .filter(Objects::nonNull)
                .map(PostMapper::toPreviewDTO)
                .toList();
    }

    // ------------------------------ Helper methods -------------------------------------

    private Category getValidCategoryIfNeeded(PostRequestDTO dto) {

        if (dto.getVisibility() == Visibility.COMMUNITY) {
            return categoryValidator.validateCategoryById(dto.getCategoryId());
        }

        return null;
    }

    private Post buildPostFromDto(PostRequestDTO dto, User user, Category category) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .visibility(dto.getVisibility())
                .category(category)
                .author(user)
                .build();
    }

    private void savePostImages(Post post, List<String> imageUrls) {

        // Ensure list is initialized (in case builder left it null)
        if (post.getImages() == null) {
            post.setImages(new ArrayList<>());
        }

        // Clear old references (orphanRemoval = true will delete them from DB)
        post.getImages().clear();

        if (imageUrls == null || imageUrls.isEmpty()) return;

        for (String url : imageUrls) {
            PostImage image = PostImage.builder()
                    .imageUrl(url)
                    .post(post)
                    .build();
            post.getImages().add(image);
        }
    }
}
