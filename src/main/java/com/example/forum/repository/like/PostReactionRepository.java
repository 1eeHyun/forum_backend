package com.example.forum.repository.like;

import com.example.forum.model.like.PostReaction;
import com.example.forum.model.like.ReactionType;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post Post, User user);
    List<PostReaction> findByPost(Post post);
    long countByPostAndReactionType(Post post, ReactionType reactionType);

    void deleteByPostId(Long postId);

    List<PostReaction> findByPostAndReactionType(Post post, ReactionType reactionType);
}
