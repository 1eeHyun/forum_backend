package com.example.forum.mapper;

import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.model.comment.Comment;

public class CommentMapper {

    public static CommentResponseDTO toDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .author(AuthorMapper.toDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .replies(comment.getReplies().stream()
                        .map(CommentMapper::toDTO)
                        .toList())
                .build();
    }
}
