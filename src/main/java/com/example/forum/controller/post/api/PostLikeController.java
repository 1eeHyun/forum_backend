package com.example.forum.controller.post.api;

import com.example.forum.controller.post.docs.PostLikeApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.service.like.post.PostLikeService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/likes")
@RequiredArgsConstructor
public class PostLikeController implements PostLikeApiDocs {

    private final AuthValidator authValidator;
    private final PostLikeService postLikeService;

    @Override
    public ResponseEntity<CommonResponse<Void>> likePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        postLikeService.toggleLike(postId, username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getLikesCount(
            @PathVariable Long postId) {

        long response = postLikeService.countLikes(postId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(
            @PathVariable Long postId) {

        List<LikeUserDTO> response = postLikeService.getLikeUsers(postId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

}
