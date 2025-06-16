package com.example.forum.repository.community;

import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCommunityAndName(Community community, String name);
}
