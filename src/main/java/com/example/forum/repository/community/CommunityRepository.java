package com.example.forum.repository.community;

import com.example.forum.model.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommunityRepository extends JpaRepository<Community, Long> {

    boolean existsByName(String name);

    List<Community> findTop5ByNameContainingIgnoreCase(String name);
}
