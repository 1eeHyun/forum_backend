package com.example.forum.dto.comment;

import com.example.forum.dto.post.AuthorDTO;
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
    private List<CommentResponseDTO> replies;
}
