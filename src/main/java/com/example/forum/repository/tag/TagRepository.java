package com.example.forum.repository.tag;

import com.example.forum.model.tag.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findByNameIgnoreCase(String name);
    List<Tag> findByNameIn(Collection<String> names);
    Page<Tag> findByNameStartingWithIgnoreCase(String prefix, Pageable pageable);

    @Query(
        """
            SELECT t.name
            FROM Post p
            JOIN p.postTags pt
            JOIN pt.tag t
            WHERE p.createdAt >= :from
            GROUP BY t.name
            ORDER BY COUNT(p.id) DESC
        """
    )
    List<String> findTopTagsSince(@Param("from") LocalDateTime from, Pageable pageable);

}
