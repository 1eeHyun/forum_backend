package com.example.forum.repository.community;

import com.example.forum.model.community.CommunityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityRuleRepository extends JpaRepository<CommunityRule, Long> {
}
