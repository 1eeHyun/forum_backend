package com.example.forum.mapper.community;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityResponseDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.mapper.auth.AuthorMapper;
import com.example.forum.model.community.Community;

public class CommunityMapper {

    public static CommunityResponseDTO toDTO(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(community.getImageUrl())
                        .imagePositionX(community.getImagePositionX())
                        .imagePositionY(community.getImagePositionY())
                        .build())
                .createdAt(community.getCreatedAt())
                .author(AuthorMapper.toDto(community.getCreator()))
                .build();
    }

    public static CommunityPreviewDTO toPreviewDTO(Community community) {

        if (community == null) return null;

        return CommunityPreviewDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(community.getImageUrl())
                        .imagePositionX(community.getImagePositionX())
                        .imagePositionY(community.getImagePositionY())
                        .build())
                .build();

    }
}
