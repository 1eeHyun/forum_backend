package com.example.forum.service.post;

import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final AuthValidator authValidator;
    private final PostRepository postRepository;
    private final PostValidator postValidator;
    private final CommunityValidator communityValidator;

    @Override
    public List<PostResponseDTO> getAllPostsByASC() {
        return postRepository.findAll().stream()
                .map(PostMapper::toDTO)
                .toList();
    }

    @Override
    public List<PostResponseDTO> getAllPostsByDESC() {
        return postRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(PostMapper::toDTO)
                .toList();
    }

    @Override
    public PostResponseDTO createPost(PostRequestDTO dto, String username) {

        User user = authValidator.validateUser(username);

        Community community = null;
        if (dto.getVisibility() == Visibility.COMMUNITY)
            community = communityValidator.validateCommunity(dto.getCommunityId(), user);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .author(user)
                .build();

        postRepository.save(post);

        return PostMapper.toDTO(post);
    }

    @Override
    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username) {

        Post post = postValidator.validatePost(postId, username);

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        return PostMapper.toDTO(post);
    }

    @Override
    public void deletePost(Long postId, String username) {

        Post post = postValidator.validatePost(postId, username);
        postRepository.delete(post);
    }
}
