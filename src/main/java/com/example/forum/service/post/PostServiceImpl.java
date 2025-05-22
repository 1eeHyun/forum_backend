package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostImage;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.post.PostImageRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.service.S3Service;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    // Validators
    private final AuthValidator authValidator;
    private final PostValidator postValidator;
    private final CommunityValidator communityValidator;

    // Repositories
    private final PostRepository postRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final PostImageRepository postImageRepository;

    // Services
    private final S3Service s3Service;

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
        return PostMapper.toPostDetailDTO(post, viewer);
    }

    @Override
    public PostResponseDTO createPost(PostRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);
        Community community = getValidCommunityIfNeeded(dto, user);
        postValidator.validatePostCount(dto.getImageUrls());

        Post post = buildPostFromDto(dto, user, community);
        postRepository.save(post);

        savePostImages(post, dto.getImageUrls());

        return PostMapper.toPostResponseDTO(post);
    }

    @Override
    @Transactional
    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);
        postValidator.validatePostAuthor(post, user);

        Community community = getValidCommunityIfNeeded(dto, user);
        postValidator.validatePostCount(dto.getImageUrls());

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setVisibility(dto.getVisibility());
        post.setCommunity(community);

        postImageRepository.deleteAll(post.getImages());
        post.getImages().clear();

        savePostImages(post, dto.getImageUrls());

        Post savedPost = postRepository.save(post);
        return PostMapper.toPostResponseDTO(savedPost);
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

    // ------------------------------ Helper methods -------------------------------------

    private Community getValidCommunityIfNeeded(PostRequestDTO dto, User user) {

        if (dto.getVisibility() == Visibility.COMMUNITY) {
            return communityValidator.validateMemberCommunity(dto.getCommunityId(), user);
        }

        return null;
    }

    private Post buildPostFromDto(PostRequestDTO dto, User user, Community community) {
        return Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .visibility(dto.getVisibility())
                .community(community)
                .author(user)
                .build();
    }

    private void savePostImages(Post post, List<String> imageUrls) {

        if (imageUrls == null) {
            post.setImages(new ArrayList<>());
            return;
        }

        List<PostImage> images = imageUrls.stream()
                .map(url -> PostImage.builder()
                        .imageUrl(url)
                        .post(post)
                        .build())
                .toList();
        post.setImages(images);

        postImageRepository.saveAll(images);
    }
}
