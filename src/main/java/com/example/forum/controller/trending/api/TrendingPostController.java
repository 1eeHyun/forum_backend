package com.example.forum.controller.trending.api;

import com.example.forum.controller.trending.docs.TrendingPostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.trending.TrendingService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class TrendingPostController implements TrendingPostApiDocs {

    private final AuthValidator authValidator;
    private final TrendingService trendingService;


    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getTrendingPosts(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails != null) ? authValidator.extractUsername(userDetails) : null;

        List<PostResponseDTO> response = trendingService.getTrendingPosts(username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
