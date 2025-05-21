package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommunityServiceImpl implements CommunityService {

    // Validators
    private final AuthValidator authValidator;
    private final CommunityValidator communityValidator;

    // Repositories
    private final CommunityRepository communityRepository;
    private final CommunityMemberRepository communityMemberRepository;

    // Services
    ///////

    // Default values
    @Value("${app.default-community-image}")
    private String defaultCommunityImageUrl;

    @Value("${app.default-banner-image}")
    private String defaultBannerImage;

    /**
     * This method handles creating a new community
     * @param dto
     * @param username
     * @return
     */
    @Override
    @Transactional
    public Long create(CommunityRequestDTO dto, String username) {

        User user = authValidator.validateUserByUsername(username);

        //
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

    @Override
    public CommunityDetailDTO getCommunityDetail(Long id, String username) {

        Community community = communityValidator.validateExistingCommunity(id);

        // Get every member
        List<CommunityMember> allMembers = communityMemberRepository.findByCommunity(community);

        // Get online members
        List<CommunityMember> onlineMembers = allMembers.stream()
                .filter(cm -> cm.getUser().isOnline())
                .toList();

        User currentUser = null;
        if (username != null)
            currentUser = authValidator.validateUserByUsername(username);

        // Check current user's role
        CommunityRole role = communityMemberRepository.findByCommunityAndUser(community, currentUser)
                .map(CommunityMember::getRole)
                .orElse(null);

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
}
