package com.example.forum.service.community.member;

import com.example.forum.dto.user.UserDTO;
import com.example.forum.exception.auth.ForbiddenException;
import com.example.forum.mapper.user.UserMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.service.auth.RedisService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class CommunityMemberServiceImpl implements CommunityMemberService {

    // Validators
    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;

    // Repositories
    private final CommunityMemberRepository communityMemberRepository;

    // Services
    private final RedisService redisService;

    //
    // ---------------- Community Member Related ----------------
    //
    @Override
    public List<UserDTO> getOnlineUsers(Long id) {

        Community community = communityValidator.validateExistingCommunity(id);
        List<CommunityMember> members = communityMemberRepository.findByCommunity(community);

        return members.stream()
                .filter(cm -> redisService.isUserOnline(cm.getUser().getId()))
                .map(UserMapper::toDtoByCommunityMember)
                .toList();
    }

    @Override
    @Transactional
    public void addMember(Long communityId, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        if (community.getMembers().contains(user))
            return;

        community.addMember(user, CommunityRole.MEMBER);
    }

    @Override
    @Transactional
    public void leaveCommunity(Long communityId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateMemberCommunity(communityId, user);

        CommunityMember member = community.getMembers().stream()
                .filter(cm -> cm.getUser().equals(user))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Membership record not found"));

        if (member.getRole() == CommunityRole.MANAGER) {
            throw new ForbiddenException("Managers cannot leave the community directly.");
        }

        community.removeMember(user);
    }

    @Override
    public List<UserDTO> getNewMembersThisWeek(Long communityId) {

        Community community = communityValidator.validateExistingCommunity(communityId);

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusDays(7);

        List<CommunityMember> recentMembers = communityMemberRepository.findByCommunityAndJoinedAtAfter(community, oneWeekAgo);

        return recentMembers.stream()
                .map(UserMapper::toDtoByCommunityMember)
                .toList();
    }

    @Override
    public List<UserDTO> getAllMembers(Long communityId, String username) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateExistingCommunity(communityId);
        communityValidator.validateManagerPermission(user, community);
        Set<CommunityMember> members = community.getMembers();

        return members.stream()
                .map(UserMapper::toDtoByCommunityMember)
                .toList();
    }
}
