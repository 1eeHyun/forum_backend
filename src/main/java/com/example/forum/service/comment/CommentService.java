package com.example.forum.service.comment;

import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;

import java.util.List;

public interface CommentService {

    CommentResponseDTO createComment(String username, CommentRequestDTO dto);
    CommentResponseDTO createReply(String username, CommentRequestDTO dto);
    List<CommentResponseDTO> getCommentsByPostId(Long postId);
    void deleteComment(Long commentId, String username);
}
