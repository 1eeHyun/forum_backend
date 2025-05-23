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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    // -------------------------------------------------------------------
    // Home Feed: All public/community posts (include PRIVATE if owner)
    // -------------------------------------------------------------------

    // Home Feed - NEWEST
    @Query(value = """
        SELECT * FROM post
        WHERE visibility = 'PUBLIC' OR visibility = 'COMMUNITY'
        ORDER BY created_at DESC, id DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Post> findPagedPostsNewest(@Param("limit") int limit, @Param("offset") int offset);

    // Home Feed - OLDEST
    @Query(value = """
        SELECT * FROM post
        WHERE visibility = 'PUBLIC' OR visibility = 'COMMUNITY'
        ORDER BY created_at ASC, id ASC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Post> findPagedPostsOldest(@Param("limit") int limit, @Param("offset") int offset);

    // Home Feed - TOP LIKED
    @Query(value = """
        SELECT p.* FROM post p
        LEFT JOIN post_likes l ON p.id = l.post_id
        WHERE p.visibility = 'PUBLIC' OR p.visibility = 'COMMUNITY'
        GROUP BY p.id
        ORDER BY COUNT(l.id) DESC, p.created_at DESC
        LIMIT :limit OFFSET :offset
    """, nativeQuery = true)
    List<Post> findPagedPostsTopLiked(@Param("limit") int limit, @Param("offset") int offset);

    @Query("""
    SELECT p FROM Post p 
    WHERE p.createdAt >= :from 
    ORDER BY SIZE(p.likes) DESC
    """)
    List<Post> findTopPostsSince(@Param("from") LocalDateTime from, Pageable pageable);


    // -------------------------------------------------------------------
    // Profile Page: Posts by a specific user
    // -------------------------------------------------------------------

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

    @Query("""
        SELECT p FROM Post p
        LEFT JOIN p.likes l
        WHERE p.author = :author
          AND (:includePrivate = true OR p.visibility <> 'PRIVATE')
        GROUP BY p
        ORDER BY COUNT(l) DESC, p.createdAt DESC
    """)
    Page<Post> findPostsByAuthorWithLikeCount(
            @Param("author") User author,
            @Param("includePrivate") boolean includePrivate,
            Pageable pageable
    );

    List<Post> findTop5ByCommunityInOrderByCreatedAtDesc(List<Community> communities);

    int countByAuthor(User author);

    // -------------------------------------------------------------------
    // Simple List: Latest posts by author (used for fallback, etc.)
    // -------------------------------------------------------------------

    List<Post> findAllByAuthorOrderByCreatedAtDesc(User author);

    // -------------------------------------------------------------------
    // Community-Scoped View: Posts visible to shared community members
    // -------------------------------------------------------------------

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

    // -------------------------------------------------------------------
    // Post Detail: Fetch post with author, profile, likes, comments
    // -------------------------------------------------------------------

    @EntityGraph(attributePaths = {
            "author", "author.profile",
            "community",
            "likes", "likes.user", "likes.user.profile",
            "comments", "comments.author", "comments.author.profile"
    })
    Optional<Post> findById(Long postId);
}
