package com.example.forum.service.reaction.post;

import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.like.PostReaction;
import com.example.forum.model.like.ReactionType;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import com.example.forum.repository.like.PostReactionRepository;
import com.example.forum.service.notification.NotificationHelper;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.post.PostValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostReactionServiceImpl implements PostReactionService {

    // Validators
    private final AuthValidator userValidator;
    private final PostValidator postValidator;

    // Repositories
    private final PostReactionRepository postReactionRepository;

    // Service
    private final NotificationHelper notificationHelper;

    @Override
    @Transactional
    public void toggleReaction(Long postId, String username, ReactionType newType) {

        User user = userValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        Optional<PostReaction> existingOpt = postReactionRepository.findByPostAndUser(post, user);

        if (existingOpt.isPresent()) {

            PostReaction existingReaction = existingOpt.get();

            if (existingReaction.getReactionType() == newType) {
                postReactionRepository.delete(existingReaction);
            } else {
                existingReaction.setReactionType(newType);
                postReactionRepository.save(existingReaction);
            }

            return;
        }

        PostReaction newReaction = new PostReaction();
        newReaction.setPost(post);
        newReaction.setUser(user);
        newReaction.setReactionType(newType);
        postReactionRepository.save(newReaction);
    }

    @Override
    public long countLikes(Long postId) {
        Post post = postValidator.validatePost(postId);

        long likeCount = postReactionRepository.countByPostAndReactionType(post, ReactionType.LIKE);
        long dislikeCount = postReactionRepository.countByPostAndReactionType(post, ReactionType.DISLIKE);

        long effectiveLikeCount = likeCount - dislikeCount;

        return Math.max(effectiveLikeCount, 0); // No less than 0
    }

    @Override
    public List<LikeUserDTO> getLikeUsers(Long postId) {

        Post post = postValidator.validatePost(postId);

        List<PostReaction> likes = postReactionRepository.findByPostAndReactionType(post, ReactionType.LIKE);

        return likes.stream()
                .map(PostMapper::toLikeUserDTO)
                .toList();
    }

    @Override
    public ReactionType getMyReaction(Long postId, String username) {

        if (username == null) return null;

        User user = userValidator.validateUserByUsername(username);
        Post post = postValidator.validatePost(postId);

        return postReactionRepository.findByPostAndUser(post, user)
                .map(PostReaction::getReactionType)
                .orElse(null);
    }
}
