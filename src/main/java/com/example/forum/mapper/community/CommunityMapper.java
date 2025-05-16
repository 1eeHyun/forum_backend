package com.example.forum.mapper.community;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityResponseDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.mapper.auth.AuthorMapper;
import com.example.forum.mapper.util.ImageMapper;
import com.example.forum.model.community.Community;

import java.util.stream.Collectors;

public class CommunityMapper {

    public static CommunityResponseDTO toDTO(Community community) {
        return CommunityResponseDTO.builder()
                .id(community.getId())
                .name(community.getName())
                .description(community.getDescription())
                .imageDTO(ImageMapper.toDto(
                        community.getImageUrl(),
                        community.getImagePositionX(),
                        community.getImagePositionY()
                ))
                .createdAt(community.getCreatedAt())
                .author(AuthorMapper.toDto(community.getCreator()))
                .members(community.getMembers().stream()
                        .map(AuthorMapper::toDto)
                        .collect(Collectors.toSet()))
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
