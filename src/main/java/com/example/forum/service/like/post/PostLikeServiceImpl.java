package com.example.forum.service.like.post;

import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.like.PostLike;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.like.PostLikeRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostLikeServiceImpl implements PostLikeService{

    private final AuthValidator userValidator;
    private final PostValidator postValidator;
    private final PostLikeRepository postLikeRepository;

    @Override
    @Transactional
    public void toggleLike(Long postId, String username) {

        User user = userValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        Optional<PostLike> existing = postLikeRepository.findByPostAndUser(post, user);

        if (existing.isPresent()) {
            post.getLikes().remove(existing.get());
            postLikeRepository.deleteByPostAndUser(post, user);
            return;
        }

        PostLike newLike = new PostLike();
        newLike.setPost(post);
        newLike.setUser(user);
        postLikeRepository.save(newLike);
    }

    @Override
    public long countLikes(Long postId) {
        Post post = postValidator.validatePost(postId);
        return postLikeRepository.countByPost(post);
    }

    @Override
    public List<LikeUserDTO> getLikeUsers(Long postId) {

        Post post = postValidator.validatePost(postId);

        List<PostLike> likes = postLikeRepository.findByPost(post);

        return likes.stream()
                .map(PostMapper::toLikeUserDTO)
                .toList();
    }
}
