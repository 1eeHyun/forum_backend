package com.example.forum.repository.post;

import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // Every post except for private
    @Query(
        """
            SELECT p FROM Post p
            JOIN FETCH p.author a
            JOIN FETCH a.profile
            LEFT JOIN FETCH p.community c
            WHERE p.visibility = 'PUBLIC' OR p.visibility = 'COMMUNITY'
            ORDER BY p.createdAt DESC
        """
    )
    List<Post> findAllNonPrivatePostsDesc();

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.author a
        JOIN FETCH a.profile
        LEFT JOIN FETCH p.community c
        WHERE p.visibility = 'PUBLIC' OR p.visibility = 'COMMUNITY'
        ORDER BY p.createdAt ASC
    """)
    List<Post> findAllNonPrivatePostsAsc();

    List<Post> findAllByAuthorOrderByCreatedAtDesc(User author);

    @Query("""
        SELECT p FROM Post p
        JOIN FETCH p.author a
        JOIN FETCH a.profile
        WHERE p.author = :author
          AND (
            p.visibility = 'PUBLIC'
            OR (p.visibility = 'COMMUNITY' AND p.community IN :sharedCommunities)
          )
        ORDER BY p.createdAt DESC
    """)
    List<Post> findVisiblePostsForViewer(
            @Param("author") User author,
            @Param("sharedCommunities") List<Community> sharedCommunities
    );


    @EntityGraph(attributePaths = {
            "author", "author.profile",
            "community",
            "likes", "likes.user", "likes.user.profile",
            "comments", "comments.author", "comments.author.profile"
    })
    Optional<Post> findById(Long postId);
}
