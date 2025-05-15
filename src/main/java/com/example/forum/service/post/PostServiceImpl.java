package com.example.forum.service.post;

import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.PostMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityRepository;
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
    private final CommunityRepository communityRepository;

    /**
     * This method returns every possible post (list form) that a user can see
     * in ascending order
     * @param username from UserDetails
     */
    @Override
    public List<PostResponseDTO> getAccessiblePostsByASC(String username) {

        List<Post> posts;

        // Check if the user logged-in
        if (username != null) {

            User user;

            // Validate user
            user = authValidator.validateUserByUsername(username);
            // Find every community of the user
            List<Community> communities = communityRepository.findAllByMembersContaining(user);
            // Add every post to a list that the user can see
            posts = postRepository.findAccessiblePosts(communities);
        } else {
            // Add every `Public` post to a list
            posts = postRepository.findAllByVisibilityOrderByCreatedAtAsc(Visibility.PUBLIC);
        }

        return posts.stream()
                .map(PostMapper::toDTO)
                .toList();
    }

    /**
     * This method returns every possible post (list form) that a user can see
     * in descending order
     * @param username from UserDetails
     */
    @Override
    public List<PostResponseDTO> getAccessiblePostsByDESC(String username) {

        List<Post> posts;

        // Check if the user logged-in
        if (username != null) {
            User user;

            // Validate the user
            user = authValidator.validateUserByUsername(username);
            // Get the user's every community
            List<Community> communities = communityRepository.findAllByMembersContaining(user);
            // Add every post to a list that the user can see
            posts = postRepository.findAccessiblePosts(communities);

        } else {
            // Add every `Public` post to a list
            posts = postRepository.findAllByVisibilityOrderByCreatedAtDesc(Visibility.PUBLIC);
        }

        return posts.stream()
                .map(PostMapper::toDTO)
                .toList();
    }

    @Override
    public PostResponseDTO createPost(PostRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);

        Community community = null;
        if (dto.getVisibility() == Visibility.COMMUNITY)
            community = communityValidator.validateMemberCommunity(dto.getCommunityId(), user);

        Post post = Post.builder()
                .title(dto.getTitle())
                .content(dto.getContent())
                .visibility(dto.getVisibility())
                .community(community)
                .author(user)
                .build();

        postRepository.save(post);

        return PostMapper.toDTO(post);
    }

    @Override
    public PostResponseDTO updatePost(Long postId, PostRequestDTO dto, String username) {

        Post post = postValidator.validatePostAuthor(postId, username);

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        Post savedPost = postRepository.save(post);
        return PostMapper.toDTO(savedPost);
    }

    @Override
    public void deletePost(Long postId, String username) {

        Post post = postValidator.validatePostAuthor(postId, username);
        postRepository.delete(post);
    }
}
