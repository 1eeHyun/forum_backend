package com.example.forum.service.community;

import com.example.forum.dto.community.*;
import com.example.forum.dto.util.UserDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.user.UserMapper;
import com.example.forum.model.community.*;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CategoryRepository;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.community.CommunityRuleRepository;
import com.example.forum.service.auth.RedisService;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityRuleValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    // Validators
    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;
    private final CommunityRuleValidator communityRuleValidator;

    // Repositories
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CategoryRepository categoryRepository;
    private final CommunityRuleRepository communityRuleRepository;

    // Services
    private final RedisService redisService;

    // Default values
    @Value("${app.default-community-image}")
    private String defaultCommunityImageUrl;

    @Value("${app.default-banner-image}")
    private String defaultBannerImage;

    //
    // ---------------- Community Related ----------------
    //
    /**
     * This method handles creating a new community
     */
    @Override
    @Transactional
    public Long create(CommunityRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);

        Community community = Community.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .creator(user)
                .profileImageUrl(defaultCommunityImageUrl)
                .bannerImageUrl(defaultBannerImage)
                .build();

        communityRepository.save(community);

        CommunityMember member = CommunityMember.builder()
                .community(community)
                .user(user)
                .role(CommunityRole.MANAGER)
                .build();

        communityMemberRepository.save(member);

        return community.getId();
    }

    /**
     * This method handles community's detail information
     */
    @Override
    public CommunityDetailDTO getCommunityDetail(Long id, String username) {
        
        Community community = communityValidator.validateExistingCommunity(id);
        List<CommunityMember> allMembers = communityMemberRepository.findByCommunity(community);
        List<CommunityMember> onlineMembers = filterOnlineMembers(allMembers);

        User currentUser = (username != null) ? authValidator.validateUserByUsername(username) : null;
        CommunityRole role = findUserRoleInCommunity(community, currentUser);

        return CommunityMapper.toDetailDTO(community, allMembers, onlineMembers, role);
    }

    @Override
    public List<CommunityPreviewDTO> getMyCommunities(String username) {

        User user = authValidator.validateUserByUsername(username);
        List<CommunityMember> memberships = communityMemberRepository.findByUser(user);

        return memberships.stream()
                .map(m -> {
                    Community c = m.getCommunity();
                    return CommunityMapper.toPreviewDTO(c);
                })
                .toList();
    }

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
    public void removeMember(Long communityId, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        if (!community.getMembers().contains(user))
            return;

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

    //
    // ---------------- Community Category Related ----------------
    //
    @Override
    public List<CategoryResponseDTO> getCategories(Long id) {

        Community community = communityValidator.validateExistingCommunity(id);

        return community.getCategories().stream()
                .map(category -> new CategoryResponseDTO(
                        category.getId(),
                        category.getName(),
                        category.getDescription()
                ))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void addCategory(Long communityId, CategoryRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateExistingCommunity(communityId);

        communityValidator.validateManagerPermission(user, community);

        Category category = Category.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .community(community)
                .build();

        categoryRepository.save(category);
        community.getCategories().add(category);
    }

    //
    // ---------------- Community Rule Related ----------------
    //
    @Override
    public List<CommunityRuleResponseDTO> getRules(Long communityId) {

        Community community = communityValidator.validateExistingCommunity(communityId);

        Set<CommunityRule> rules = community.getRules();
        return rules.stream()
                .sorted(Comparator.comparing(CommunityRule::getCreatedAt))
                .map(CommunityMapper::toRuleResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public void addRule(Long communityId, CommunityRuleRequestDTO request, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateManagerPermission(user, community);

        CommunityRule newRule = CommunityRule.builder()
                .title(request.getTitle())
                .community(community)
                .content(request.getContent())
                .build();

        communityRuleRepository.save(newRule);
    }

    @Override
    @Transactional
    public void updateRule(Long communityId, Long ruleId, CommunityRuleRequestDTO request, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateManagerPermission(user, community);

        CommunityRule rule = communityRuleValidator.validateExistingRuleInCommunity(ruleId, community);

        rule.setTitle(request.getTitle());
        rule.setContent(request.getContent());
        communityRuleRepository.save(rule);
    }

    @Override
    @Transactional
    public void deleteRule(Long communityId, Long ruleId, String username) {

        Community community = communityValidator.validateExistingCommunity(communityId);
        User user = authValidator.validateUserByUsername(username);

        communityValidator.validateManagerPermission(user, community);

        CommunityRule rule = communityRuleValidator.validateExistingRuleInCommunity(ruleId, community);
        communityRuleRepository.deleteById(rule.getId());
    }

    /*****************************************************
     * Helper methods
     */
    private List<CommunityMember> filterOnlineMembers(List<CommunityMember> members) {
        return members.stream()
                .filter(cm -> cm.getUser().isOnline())
                .toList();
    }

    private CommunityRole findUserRoleInCommunity(Community community, User user) {
        if (user == null) return null;
        return communityMemberRepository.findByCommunityAndUser(community, user)
                .map(CommunityMember::getRole)
                .orElse(null);
    }
}
