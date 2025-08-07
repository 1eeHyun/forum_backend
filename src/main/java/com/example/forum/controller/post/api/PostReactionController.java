package com.example.forum.controller.post.api;

import com.example.forum.controller.post.docs.PostReactionApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.model.like.ReactionType;
import com.example.forum.service.reaction.post.PostReactionService;
import com.example.forum.validator.auth.AuthValidator;
import io.swagger.v3.oas.models.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}")
@RequiredArgsConstructor
public class PostReactionController implements PostReactionApiDocs {

    private final AuthValidator authValidator;
    private final PostReactionService postReactionService;

    @Override
    public ResponseEntity<CommonResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        postReactionService.toggleReaction(postId, username, ReactionType.LIKE);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> dislikePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        postReactionService.toggleReaction(postId, username, ReactionType.DISLIKE);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getLikesCount(
            @PathVariable Long postId) {

        long response = postReactionService.countLikes(postId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<String>> getMyReaction(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails != null) ? authValidator.extractUsername(userDetails) : null;
        ReactionType reaction = postReactionService.getMyReaction(postId, username);

        String reactionString = (reaction != null) ? reaction.name() : null;

        return ResponseEntity.ok(CommonResponse.success(reactionString));
    }

    @Override
    public ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(
            @PathVariable Long postId) {

        List<LikeUserDTO> response = postReactionService.getLikeUsers(postId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

}
