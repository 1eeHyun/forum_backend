package com.example.forum.controller.post.api;

import com.example.forum.controller.post.docs.PostViewApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.service.post.PostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostViewController implements PostViewApiDocs {

    private final AuthValidator authValidator;
    private final PostService postService;

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getRecentlyViewedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) List<Long> localIds) {

        if (userDetails != null) {
            String username = authValidator.extractUsername(userDetails);
            List<PostPreviewDTO> response = postService.getRecentlyViewedPosts(username);

            return ResponseEntity.ok(CommonResponse.success(response));
        }

        if (localIds != null && !localIds.isEmpty()) {
            List<PostPreviewDTO> response = postService.getPreviewPostsByIds(localIds, null);

            return ResponseEntity.ok(CommonResponse.success(response));
        }

        return ResponseEntity.ok(CommonResponse.success(Collections.emptyList()));
    }
}
