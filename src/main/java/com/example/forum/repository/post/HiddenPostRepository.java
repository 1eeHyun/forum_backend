package com.example.forum.repository.post;

import com.example.forum.model.post.HiddenPost;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HiddenPostRepository extends JpaRepository<HiddenPost, Long> {

    boolean existsByUserAndPost(User user, Post post);
    void deleteByUserAndPost(User user, Post post);
    Optional<HiddenPost> findByUserAndPost(User user, Post post);
    List<HiddenPost> findAllByUser(User user);

    @Query("select h.post.id from HiddenPost h where h.user = :user")
    List<Long> findHiddenPostIdsByUser(@Param("user") User user);
}
