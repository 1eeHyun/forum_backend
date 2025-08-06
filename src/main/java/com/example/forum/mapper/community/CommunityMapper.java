package com.example.forum.mapper.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;
import com.example.forum.dto.image.ImageDTO;
import com.example.forum.mapper.image.ImageMapper;
import com.example.forum.mapper.user.UserMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;
import com.example.forum.model.community.CommunityRule;

import java.util.Comparator;
import java.util.List;

public class CommunityMapper {

    public static CommunityDetailDTO toDetailDTO(
            Community community,
            List<CommunityMember> allMembers,
            List<CommunityMember> onlineMembers,
            CommunityRole currentUserRole
    ) {
        return CommunityDetailDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .profileImageDto(ImageMapper.toDto(
                        community.getProfileImageUrl(),
                        community.getProfileImagePositionX(),
                        community.getProfileImagePositionY()))
                .bannerImageUrl(community.getBannerImageUrl())
                .createdAt(community.getCreatedAt())
                .rules(
                        community.getRules().stream()
                                .sorted(Comparator.comparing(CommunityRule::getCreatedAt))
                                .map(CommunityMapper::toRuleResponseDto)
                                .toList()
                )
                .categories(
                        community.getCategories().stream()
                                .sorted(Comparator.comparing(category -> category.getName().toLowerCase()))
                                .map(CategoryMapper::toDTO)
                                .toList()
                )
                .onlineCount(onlineMembers.size())
                .memberCount(allMembers.size())
                .role(currentUserRole)
                .onlineUsers(UserMapper.toListDtoByCommunityMemberList(onlineMembers))
                .build();
    }

    public static CommunityPreviewDTO toPreviewDTO(Community community, boolean isFavorite) {

        if (community == null) return null;

        return CommunityPreviewDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(community.getProfileImageUrl())
                        .imagePositionX(community.getProfileImagePositionX())
                        .imagePositionY(community.getProfileImagePositionY())
                        .build())
                .isFavorite(isFavorite)
                .build();

    }

    public static CommunityRuleResponseDTO toRuleResponseDto(CommunityRule rule) {

        return CommunityRuleResponseDTO.builder()
                .id(rule.getId())
                .title(rule.getTitle())
                .content(rule.getContent())
                .createdAt(rule.getCreatedAt())
                .build();
    }
}
