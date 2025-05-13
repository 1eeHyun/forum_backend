package com.example.forum.mapper;

import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.model.post.Post;

public class PostMapper {
    public static PostResponseDTO toDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .authorNickname(post.getAuthor().getProfile().getNickname())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
