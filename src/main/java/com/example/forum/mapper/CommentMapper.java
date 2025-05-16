package com.example.forum.mapper;

import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.model.comment.Comment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentMapper {

    public static CommentResponseDTO toDTO(Comment comment) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .author(AuthorMapper.toDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .replies(comment.getReplies() != null
                        ? comment.getReplies().stream().map(CommentMapper::toDTO).toList()
                        : new ArrayList<>())
                .likeCount(comment.getLikeCount())
                .dislikeCount(comment.getDislikeCount())
                .build();
    }

//    private Long commentId;
//    private String content;
//    private AuthorDTO author;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private List<CommentResponseDTO> replies;

    public static List<CommentResponseDTO> toResponseList(List<Comment> allComments) {

        Map<Long, List<Comment>> replyMap = new HashMap<>();
        List<Comment> rootComments = new ArrayList<>();

        for (Comment comment : allComments) {
            if (comment.getParentComment() == null) {
                rootComments.add(comment);
            } else {
                Long parentId = comment.getParentComment().getId();
                replyMap.computeIfAbsent(parentId, k -> new ArrayList<>()).add(comment);
            }
        }

        return rootComments.stream()
                .map(c -> toDTORecursive(c, replyMap))
                .toList();
    }

    private static CommentResponseDTO toDTORecursive(Comment comment, Map<Long, List<Comment>> replyMap) {
        return CommentResponseDTO.builder()
                .commentId(comment.getId())
                .content(comment.getContent())
                .author(AuthorMapper.toDto(comment.getAuthor()))
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .replies(replyMap.getOrDefault(comment.getId(), new ArrayList<>())
                        .stream()
                        .map(child -> toDTORecursive(child, replyMap))
                        .toList())
                .build();
    }
}
