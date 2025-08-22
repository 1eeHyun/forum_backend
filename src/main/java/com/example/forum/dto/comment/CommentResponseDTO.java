package com.example.forum.dto.comment;

import com.example.forum.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@Builder
public class CommentResponseDTO {

    private Long commentId;
    private String content;
    private UserDTO author;
    private Instant createdAt;
    private Instant updatedAt;
    private List<CommentResponseDTO> replies;

    private long likeCount;
    private long dislikeCount;
}
