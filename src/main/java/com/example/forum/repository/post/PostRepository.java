package com.example.forum.repository.post;

import com.example.forum.model.community.Community;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = { "author", "author.profile", "community" })
    @Query(
        """
            SELECT p FROM Post p
            WHERE p.visibility = 'PUBLIC' OR p.visibility = 'COMMUNITY'
        """
    )
    Page<Post> findAllNonPrivate(Pageable pageable);

    @Query(value =
        """
            SELECT * FROM post
            WHERE visibility = 'PUBLIC' OR visibility = 'COMMUNITY'
            ORDER BY created_at DESC, id DESC
            LIMIT :limit OFFSET :offset
        """,
            nativeQuery = true
    )
    List<Post> findPagedPosts(@Param("limit") int limit, @Param("offset") int offset);

    @Query("""
    SELECT p FROM Post p
    WHERE p.author = :author
      AND (:includePrivate = true OR p.visibility <> 'PRIVATE')
""")
    Page<Post> findPostsByAuthor(
            @Param("author") User author,
            @Param("includePrivate") boolean includePrivate,
            Pageable pageable
    );

    @Query(
        """
            SELECT p FROM Post p
            LEFT JOIN p.likes l
            WHERE p.author = :author
              AND (:includePrivate = true OR p.visibility <> 'PRIVATE')
            GROUP BY p
            ORDER BY COUNT(l) DESC, p.createdAt DESC
        """
    )
    List<Post> findPostsByAuthorOrderByLikeCount(
            @Param("author") User author,
            @Param("includePrivate") boolean includePrivate
    );


    List<Post> findAllByAuthorOrderByCreatedAtDesc(User author);

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

    @EntityGraph(attributePaths = {
            "author", "author.profile",
            "community",
            "likes", "likes.user", "likes.user.profile",
            "comments", "comments.author", "comments.author.profile"
    })
    Optional<Post> findById(Long postId);

    @Query(
        """
            SELECT p FROM Post p
            JOIN FETCH p.author a
            JOIN FETCH a.profile
            LEFT JOIN FETCH p.community c
            LEFT JOIN p.likes l
            WHERE p.visibility = 'PUBLIC' OR p.visibility = 'COMMUNITY'
            GROUP BY p
            ORDER BY COUNT(l) DESC, p.createdAt DESC
        """
    )
    List<Post> findAllNonPrivatePostsWithLikeCountDesc();

}
