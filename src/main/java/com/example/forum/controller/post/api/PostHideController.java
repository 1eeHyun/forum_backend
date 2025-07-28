package com.example.forum.controller.post.api;

import com.example.forum.controller.post.docs.PostHideApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.service.post.PostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostHideController implements PostHideApiDocs {

    private final AuthValidator authValidator;
    private final PostService postService;

    @Override
    public ResponseEntity<CommonResponse<Void>> toggleHidePost(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        postService.toggleHidePost(postId, username);
        return ResponseEntity.ok(CommonResponse.success());
    }
}
