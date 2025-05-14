package com.example.forum.mapper;

import com.example.forum.dto.community.CommunityResponseDTO;
import com.example.forum.model.community.Community;

public class CommunityMapper {

    public static CommunityResponseDTO toDTO(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .createdAt(community.getCreatedAt())
                .author(AuthorMapper.toDto(community.getCreator()))
                .build();
    }
}
