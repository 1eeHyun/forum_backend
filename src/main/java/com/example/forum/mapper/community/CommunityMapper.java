package com.example.forum.mapper.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.dto.util.OnlineUserDTO;
import com.example.forum.mapper.util.ImageMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.community.CommunityRole;

import java.util.List;

public class CommunityMapper {

    public static CommunityDetailDTO toDetailDTO(Community community, List<CommunityMember> allMembers, List<CommunityMember> onlineMembers, CommunityRole role) {
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
                .rules(community.getRules().stream().toList())
                .categories(community.getCategories().stream().toList())
                .onlineCount(onlineMembers.size())
                .memberCount(allMembers.size())
                .role(role)
                .onlineUsers(onlineMembers.stream()
                        .map(cm -> new OnlineUserDTO(
                                cm.getUser().getId(),
                                cm.getUser().getProfile().getNickname(),
                                ImageMapper.toDto(
                                        cm.getUser().getProfile().getImageUrl(),
                                        cm.getUser().getProfile().getImagePositionX(),
                                        cm.getUser().getProfile().getImagePositionY())
                        ))
                        .toList()
                )
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
