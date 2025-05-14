package com.example.forum.repository.post;

import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    @Query(
            """
                SELECT p FROM Post p
                JOIN FETCH p.author a
                JOIN FETCH a.profile
                WHERE p.visibility = :visibility
                ORDER BY p.createdAt ASC
            """
    )
    List<Post> findAllByVisibilityOrderByCreatedAtAsc(Visibility visibility);

    @Query(
            """
                SELECT p FROM Post p
                JOIN FETCH p.author a
                JOIN FETCH a.profile
                WHERE p.visibility = :visibility
                ORDER BY p.createdAt DESC
            """
    )
    List<Post> findAllByVisibilityOrderByCreatedAtDesc(Visibility visibility);

}
