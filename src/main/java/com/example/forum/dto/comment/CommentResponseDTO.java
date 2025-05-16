package com.example.forum.dto.comment;

import com.example.forum.dto.util.AuthorDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private AuthorDTO author;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<CommentResponseDTO> replies;

    private int likeCount;
    private int dislikeCount;
}
