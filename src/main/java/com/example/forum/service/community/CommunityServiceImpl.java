package com.example.forum.service.community;

import com.example.forum.dto.community.*;
import com.example.forum.dto.util.OnlineUserDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.util.OnlineUserMapper;
import com.example.forum.model.community.Category;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CategoryRepository;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    // Validators
    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;

    // Repositories
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;
    private final CategoryRepository categoryRepository;

    // Services
    ///////

    // Default values
    @Value("${app.default-community-image}")
    private String defaultCommunityImageUrl;

    @Value("${app.default-banner-image}")
    private String defaultBannerImage;

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

    @Override
    public List<OnlineUserDTO> getOnlineUsers(Long id) {

        Community community = communityValidator.validateExistingCommunity(id);
        List<CommunityMember> members = communityMemberRepository.findByCommunity(community);

        return members.stream()
                .filter(cm -> cm.getUser().isOnline())
                .map(OnlineUserMapper::toDTO)
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
