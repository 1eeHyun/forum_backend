package com.example.forum.service.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
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
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final AuthValidator authValidator;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final CommunityValidator communityValidator;
    private final CommunityMemberRepository communityMemberRepository;
    private final PostImageRepository postImageRepository;

    private final S3Service s3Service;


    @Override
    public List<PostResponseDTO> getAccessiblePosts(SortOrder sortOrder) {
        List<Post> posts = sortOrder == SortOrder.ASCENDING
                ? postRepository.findAllNonPrivatePostsAsc()
                : postRepository.findAllNonPrivatePostsDesc();

        return posts.stream()
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

        Community community = null;
        if (dto.getVisibility() == Visibility.COMMUNITY)
            community = communityValidator.validateMemberCommunity(dto.getCommunityId(), user);

        postValidator.validatePostCount(dto.getImageUrls());

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .visibility(dto.getVisibility())
                .community(community)
                .author(user)
                .build();

        postRepository.save(post);


        if (dto.getImageUrls() != null) {
            List<PostImage> images = dto.getImageUrls().stream()
                            .map(url -> PostImage.builder()
                                    .imageUrl(url)
                                    .post(post)
                                    .build())
                            .toList();

            post.setImages(images);
            postImageRepository.saveAll(images);
        }

        return PostMapper.toPostResponseDTO(post);
    }

    @Override
    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username) {

        Post post = postValidator.validatePostAuthor(postId, username);

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        Post savedPost = postRepository.save(post);
        return PostMapper.toPostResponseDTO(savedPost);
    }

    @Override
    public void deletePost(Long postId, String username) {

        Post post = postValidator.validatePostAuthor(postId, username);
        postRepository.delete(post);
    }

    @Override
    public String uploadImage(MultipartFile file) {
        return s3Service.upload(file);
    }
}
