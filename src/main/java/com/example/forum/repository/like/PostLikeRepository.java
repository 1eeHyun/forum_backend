package com.example.forum.repository.like;

import com.example.forum.model.like.PostLike;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    Optional<PostLike> findByPostAndUser(Post post, User user);
    long countByPost(Post post);
    boolean existsByPostAndUser(Post post, User user);
    void deleteByPostAndUser(Post Post, User user);
    List<PostLike> findByPost(Post post);
}
