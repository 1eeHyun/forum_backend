package com.example.forum.controller.comment;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.exception.auth.NotAuthorizedException;
import com.example.forum.service.comment.CommentService;
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

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<CommentResponseDTO>> create(
            @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) {
            throw new NotAuthorizedException();
        }

        CommentResponseDTO response = commentService.createComment(userDetails.getUsername(), dto);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/{postId}")
    public ResponseEntity<CommonResponse<List<CommentResponseDTO>>> getAllComments(
            @PathVariable Long postId) {

        List<CommentResponseDTO> response = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PostMapping("/reply")
    public ResponseEntity<CommonResponse<CommentResponseDTO>> reply(
            @RequestBody CommentRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        CommentResponseDTO response = commentService.createReply(userDetails.getUsername(), dto);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @DeleteMapping("/{commentId}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        commentService.deleteComment(commentId, userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success());
    }
}
