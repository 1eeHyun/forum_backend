package com.example.forum.mapper;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityResponseDTO;
import com.example.forum.model.community.Community;

public class CommunityMapper {

    public static CommunityResponseDTO toDTO(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .imageUrl(community.getImageUrl())
                .createdAt(community.getCreatedAt())
                .author(AuthorMapper.toDto(community.getCreator()))
                .build();
    }

    public static CommunityPreviewDTO toPreviewDTO(Community community) {

        if (community == null) return null;

        return CommunityPreviewDTO.builder()
                .id(community.getId())
                .imageUrl(community.getImageUrl())
                .name(community.getName())
                .build();

    }
}
