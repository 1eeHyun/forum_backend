package com.example.forum.mapper.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.mapper.util.ImageMapper;
import com.example.forum.mapper.util.OnlineUserMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;

import java.util.ArrayList;
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
                .rules(new ArrayList<>(community.getRules()))
                .categories(
                        community.getCategories().stream()
                                .map(CategoryMapper::toDTO)
                                .toList()
                )
                .onlineCount(onlineMembers.size())
                .memberCount(allMembers.size())
                .role(currentUserRole)
                .onlineUsers(OnlineUserMapper.toDTO(onlineMembers))
                .build();
    }

    public static CommunityPreviewDTO toPreviewDTO(Community community) {

        if (community == null) return null;

        return CommunityPreviewDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(community.getProfileImageUrl())
                        .imagePositionX(community.getProfileImagePositionX())
                        .imagePositionY(community.getProfileImagePositionY())
                        .build())
                .build();

    }
}
