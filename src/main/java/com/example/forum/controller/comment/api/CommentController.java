package com.example.forum.controller.comment.api;

import com.example.forum.controller.comment.docs.CommentApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.service.comment.CommentService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController implements CommentApiDocs {

    private final CommentService commentService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<CommentResponseDTO>> create(
            @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        CommentResponseDTO response = commentService.createComment(username, dto);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<CommentResponseDTO>>> getAllComments(
            @PathVariable Long postId) {

        List<CommentResponseDTO> response = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<CommentResponseDTO>> reply(
            @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        CommentResponseDTO response = commentService.createReply(username, dto);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        commentService.deleteComment(commentId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
