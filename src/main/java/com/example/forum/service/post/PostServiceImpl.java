package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostFileDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.HiddenPost;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostFile;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.post.HiddenPostRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.common.RecentViewService;
import com.example.forum.service.common.S3Service;
import com.example.forum.service.post.hidden.HiddenPostService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CategoryValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    // Validators
    private final AuthValidator authValidator;
    private final PostValidator postValidator;
    private final CategoryValidator categoryValidator;
    private final CommunityValidator communityValidator;

    // Repositories
    private final PostRepository postRepository;
    private final HiddenPostRepository hiddenPostRepository;
    private final CommunityFavoriteRepository communityFavoriteRepository;

    // Services
    private final S3Service s3Service;
    private final RecentViewService recentViewService;

    private final HiddenPostService hiddenPostService;

    @Override
    public List<PostResponseDTO> getPagedPosts(SortOrder sort, int page, int size, String username) {

        int offset = page * size;
        List<Post> posts = switch (sort) {
            case NEWEST -> postRepository.findPagedPostsNewest(size, offset);
            case OLDEST -> postRepository.findPagedPostsOldest(size, offset);
            case TOP_LIKED -> postRepository.findPagedPostsTopLiked(size, offset);
        };

        User user = (username != null) ? authValidator.validateUserByUsername(username) : null;

        Set<Long> hiddenPostIds = hiddenPostService.getHiddenPostIdsByUsername(username);
        Set<Long> favoriteCommunityIds = (user != null)
                ? communityFavoriteRepository.findAllByUser(user).stream()
                .map(fav -> fav.getCommunity().getId())
                .collect(Collectors.toSet())
                : Set.of();


        return posts.stream()
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

    @Override
    public PostDetailDTO getPostDetail(Long postId, String username) {

        User viewer = (username != null) ? authValidator.validateUserByUsername(username) : null;
        Post post = postValidator.validateDetailPostId(postId);
        boolean isHidden = hiddenPostService.isHiddenByUsername(post, username);

        Community community = post.getCategory() != null ? post.getCategory().getCommunity() : null;
        boolean isFavorite = (community != null && viewer != null) &&
                communityFavoriteRepository.existsByUserAndCommunity(viewer, community);

        if (viewer != null)
            recentViewService.addPostView(viewer.getId(), postId);

        return PostMapper.toPostDetailDTO(post, viewer, isHidden, isFavorite);
    }

    @Override
    @Transactional
    public PostResponseDTO createPost(PostRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);
        Category category = getValidCategoryIfNeeded(dto);
        postValidator.validatePostCount(dto.getFileUrls());

        if (category != null)
            communityValidator.validateMemberCommunity(category.getCommunity().getId(), user);

        Post post = buildPostFromDto(dto, user, category);
        savePostFiles(post, dto.getFileUrls());

        Post savedPost = postRepository.save(post);
        return PostMapper.toPostResponseDTO(savedPost, false, false);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username) {

        // 1. Get user and post
        User user = authValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        postValidator.validatePostAuthor(post, user);

        // 2. Validate image count and community (optional)
        postValidator.validatePostCount(dto.getFileUrls());
        Category category = getValidCategoryIfNeeded(dto);

        // 3. Delete old images from S3
        List<String> oldImageUrls = post.getFiles() == null ? List.of()
                : post.getFiles().stream().map(PostFile::getFileUrl).toList();
        s3Service.deleteFiles(oldImageUrls);

        // 4. Update fields
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setVisibility(dto.getVisibility());
        post.setCategory(category);

        // 5. Update files safely
        if (post.getFiles() != null) post.getFiles().clear();
        if (dto.getFileUrls() != null) {
            for (PostFileDTO postFile : dto.getFileUrls()) {
                PostFile file = PostFile.builder()
                        .fileUrl(postFile.getFileUrl())
                        .type(postFile.getType())
                        .post(post)
                        .build();
                post.getFiles().add(file);
            }
        }

        // 6. Optional: explicit save (if needed by repository listeners)
        Post saved = postRepository.save(post); // make sure Hibernate flushes

        // 7. Determine isFavorite
        Community community = saved.getCategory() != null ? saved.getCategory().getCommunity() : null;
        boolean isFavorite = community != null &&
                communityFavoriteRepository.existsByUserAndCommunity(user, community);

        // 8. Return response
        return PostMapper.toPostResponseDTO(saved, false, isFavorite); // or just `post`
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
    public void toggleHidePost(Long postId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        Optional<HiddenPost> hiddenPostOpt = hiddenPostRepository.findByUserAndPost(user, post);

        if (hiddenPostOpt.isPresent())
            hiddenPostRepository.delete(hiddenPostOpt.get());
        else {
            HiddenPost hiddenPost = new HiddenPost(user, post);
            hiddenPostRepository.save(hiddenPost);
        }
    }

    @Override
    public List<Long> getHiddenPostIds(String username) {

        User user = authValidator.validateUserByUsername(username);
        return hiddenPostRepository.findHiddenPostIdsByUser(user);
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

    private void savePostFiles(Post post, List<PostFileDTO> fileDTOs) {

        // Ensure list is initialized (in case builder left it null)
        if (post.getFiles() == null) {
            post.setFiles(new ArrayList<>());
        }

        // Clear old references (orphanRemoval = true will delete them from DB)
        post.getFiles().clear();

        if (fileDTOs == null || fileDTOs.isEmpty()) return;

        for (PostFileDTO postFile : fileDTOs) {
            PostFile file = PostFile.builder()
                    .fileUrl(postFile.getFileUrl())
                    .type(postFile.getType())
                    .post(post)
                    .build();
            post.getFiles().add(file);
        }
    }
}
