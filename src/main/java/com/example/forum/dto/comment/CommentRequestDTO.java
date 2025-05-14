package com.example.forum.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {

    private Long postId;
    private String content;
    private Long parentCommentId; // reply -> parent Id
}
