package com.example.forum.repository.post;

import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.Visibility;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

    // Every post that a user created
    List<Post> findAllByAuthorOrderByCreatedAtDesc(User author);

    // Posts for viewers(see another person's profile)
    @Query(
        """
            SELECT p FROM Post p
            JOIN FETCH p.author a
            JOIN FETCH a.profile
            WHERE p.author = :author
            AND (
                p.visibility = 'PUBLIC'
                OR (p.visibility = 'COMMUNITY' AND p.community IN :sharedCommunities)
            )
            ORDER BY p.createdAt DESC
        """
    )
    List<Post> findVisiblePostsForViewer(
            @Param("author") User author,
            @Param("sharedCommunities") List<Community> sharedCommunities
    );

    @Query(
        """
            SELECT p FROM Post p
            JOIN FETCH p.author a
            JOIN FETCH a.profile
            LEFT JOIN FETCH p.community c
            WHERE p.visibility = 'PUBLIC'
               OR (p.visibility = 'COMMUNITY' AND p.community IN :communities)
            ORDER BY p.createdAt DESC
        """
    )
    List<Post> findAccessiblePosts(@Param("communities") List<Community> communities);


    @Query(
        """
            SELECT p FROM Post p
            JOIN FETCH p.author a
            JOIN FETCH a.profile
            LEFT JOIN FETCH p.community c
            WHERE p.visibility = 'PUBLIC'
               OR (p.visibility = 'COMMUNITY' AND p.community IN :communities)
            ORDER BY p.createdAt ASC
        """
    )
    List<Post> findAccessiblePostsAsc(@Param("communities") List<Community> communities);

}
