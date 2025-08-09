package com.example.forum.repository.post;

import com.example.forum.model.post.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PostQueryRepository extends JpaRepository<Post, Long> {

    @Query("""
        SELECT DISTINCT p FROM Post p
        LEFT JOIN FETCH p.postTags pt
        LEFT JOIN FETCH pt.tag
        WHERE p.id = :id
    """)
    Optional<Post> findByIdWithTags(@Param("id") Long id);
}
