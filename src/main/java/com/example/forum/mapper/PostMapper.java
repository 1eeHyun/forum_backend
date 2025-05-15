package com.example.forum.mapper;

import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.model.post.Post;

public class PostMapper {
    public static PostResponseDTO toDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(AuthorMapper.toDto(post.getAuthor()))
                .community(CommunityMapper.toPreviewDTO(post.getCommunity()))
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}
