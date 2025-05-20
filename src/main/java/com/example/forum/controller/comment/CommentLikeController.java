package com.example.forum.controller.comment;

import com.example.forum.dto.CommonResponse;
import com.example.forum.service.like.comment.CommentLikeService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comments/{commentId}")
public class CommentLikeController implements CommentLikeAPIDocs {

    private final CommentLikeService commentLikeService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/likes")
    public ResponseEntity<CommonResponse<Void>> likeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        commentLikeService.toggleLike(commentId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @PostMapping("/dislikes")
    public ResponseEntity<CommonResponse<Void>> dislikeComment(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        commentLikeService.toggleDislike(commentId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/likes/count")
    public ResponseEntity<CommonResponse<Long>> countLikes(
            @PathVariable Long commentId) {

        long ret = commentLikeService.countLikes(commentId);
        return ResponseEntity.ok(CommonResponse.success(ret));
    }

    @Override
    @GetMapping("/dislikes/count")
    public ResponseEntity<CommonResponse<Long>> countDislikes(
            @PathVariable Long commentId) {

        long ret = commentLikeService.countDislikes(commentId);
        return ResponseEntity.ok(CommonResponse.success(ret));
    }

    @Override
    @GetMapping("/likes/me")
    public ResponseEntity<CommonResponse<Boolean>> hasUserLiked(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails == null ? null : authValidator.extractUsername(userDetails);;
        boolean ret = commentLikeService.hasUserLiked(commentId, username);

        return ResponseEntity.ok(CommonResponse.success(ret));
    }

    @Override
    @GetMapping("/dislikes/me")
    public ResponseEntity<CommonResponse<Boolean>> hasUserDisliked(
            @PathVariable Long commentId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails == null ? null : authValidator.extractUsername(userDetails);
        boolean ret = commentLikeService.hasUserDisliked(commentId, username);

        return ResponseEntity.ok(CommonResponse.success(ret));
    }
}
