package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityFavorite;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.user.User;
import com.example.forum.repository.community.CommunityFavoriteRepository;
import com.example.forum.repository.community.CommunityMemberRepository;
import com.example.forum.repository.community.CommunityRepository;
import com.example.forum.repository.post.PostRepository;
import com.example.forum.validator.auth.AuthValidator;
import com.example.forum.validator.community.CommunityValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
    private final CommunityFavoriteRepository communityFavoriteRepository;
    private final PostRepository postRepository;

    // Services
//    private final RedisService redisService;

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

        int postCount = postRepository.countByCategoryCommunityId(id);

        return CommunityMapper.toDetailDTO(community, allMembers, onlineMembers, role, postCount);
    }

    @Override
    public List<CommunityPreviewDTO> getMyCommunities(String username) {

        User user = authValidator.validateUserByUsername(username);
        List<CommunityMember> memberships = communityMemberRepository.findByUser(user);

        Set<Long> favoriteCommunityIds = communityFavoriteRepository.findAllByUser(user).stream()
                .map(fav -> fav.getCommunity().getId())
                .collect(Collectors.toSet());

        return memberships.stream()
                .map(m -> {
                    Community c = m.getCommunity();
                    boolean isFavorite = favoriteCommunityIds.contains(c.getId());
                    return CommunityMapper.toPreviewDTO(c, isFavorite);
                })
                .toList();
    }

    @Override
    public List<CommunityPreviewDTO> getJoinedCommunities(String target, String currentUsername) {

        User targetUser = authValidator.validateUserByUsername(target);
        User currentUser = authValidator.validateUserByUsername(currentUsername);

        Set<Long> favoriteCommunityIds = communityFavoriteRepository.findAllByUser(currentUser).stream()
                .map(fav -> fav.getCommunity().getId())
                .collect(Collectors.toSet());

        List<CommunityMember> joinedMemberships = communityMemberRepository.findByUser(targetUser);

        return joinedMemberships.stream()
                .map(CommunityMember::getCommunity)
                .map(community -> CommunityMapper.toPreviewDTO(
                        community,
                        favoriteCommunityIds.contains(community.getId())
                ))
                .toList();
    }

    @Override
    @Transactional
    public void toggleFavorite(String username, Long id) {

        User user = authValidator.validateUserByUsername(username);
        Community community = communityValidator.validateExistingCommunity(id);

        Optional<CommunityFavorite> favoriteOpt = communityFavoriteRepository.findByUserAndCommunity(user, community);

        if (favoriteOpt.isPresent()) {
            communityFavoriteRepository.delete(favoriteOpt.get());
        } else {
            CommunityFavorite favorite = CommunityFavorite.builder()
                    .user(user)
                    .community(community)
                    .build();
            communityFavoriteRepository.save(favorite);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommunityPreviewDTO> getFavoriteCommunities(String username) {

        User user = authValidator.validateUserByUsername(username);

        return communityFavoriteRepository.findAllByUser(user).stream()
                .map(favorite -> CommunityMapper.toPreviewDTO(favorite.getCommunity(), true))
                .toList();
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
