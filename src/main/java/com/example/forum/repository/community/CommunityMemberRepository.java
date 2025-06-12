package com.example.forum.repository.community;

import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

    Optional<CommunityMember> findByCommunityAndUser(Community community, User user);

    // Retrieve all members of an existing community
    List<CommunityMember> findByCommunity(Community community);

    List<CommunityMember> findByUser(User user);
    List<CommunityMember> findByCommunityAndJoinedAtAfter(Community community, LocalDateTime joinedAt);


    List<CommunityMember> findByCommunityAndRole(Community community, CommunityRole role);

    long countByCommunity(Community community);
    Optional<CommunityMember> findByCommunityIdAndUserId(Long communityId, Long userId);

}
