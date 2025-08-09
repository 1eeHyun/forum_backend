package com.example.forum.repository.post;

import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostTag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostTagRepository extends JpaRepository<PostTag, Long> {

    @Query("select pt from PostTag pt join fetch pt.tag where pt.post.id = :postId")
    List<PostTag> findByPostIdWithTag(@Param("postId") Long postId);

    boolean existsByPostIdAndTagId(Long postId, Long tagId);
    void deleteByPostIdAndTagIdIn(Long postId, Collection<Long> tagIds);

    @Query("select p from PostTag pt join pt.post p join pt.tag t where lower(t.name) = lower(:tag) order by p.createdAt desc")
    Page<Post> findPostsByTag(@Param("tag") String tag, Pageable pageable);

    @Query(
            value = """
        SELECT t.name
        FROM post_tags pt
        JOIN tags t ON t.id = pt.tag_id
        GROUP BY t.id, t.name
        ORDER BY COUNT(*) DESC
        LIMIT :limit
    """,
            nativeQuery = true
    )
    List<String> findTopTagNames(@Param("limit") int limit);

    @Query("select count(pt) from PostTag pt where pt.post.id = :postId")
    long countByPostId(@Param("postId") Long postId);
}
