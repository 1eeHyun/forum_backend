package com.example.forum.repository.community;

import com.example.forum.model.community.Community;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    List<Community> findTop5ByNameContainingIgnoreCase(String name);

    @Query(value =
            """
                SELECT c.*
                FROM community_members cm
                JOIN community c ON cm.community_id = c.id
                WHERE cm.joined_at >= :from
                GROUP BY c.id
                ORDER BY COUNT(*) DESC
            """,
            nativeQuery = true)
    List<Community> findTrendingCommunities(
            @Param("from") LocalDateTime from,
            Pageable pageable
    );
}
