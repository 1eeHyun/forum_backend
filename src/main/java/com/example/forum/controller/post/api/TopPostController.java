package com.example.forum.controller.post.api;

import com.example.forum.controller.post.docs.TopPostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostPreviewDTO;
<<<<<<< HEAD:src/main/java/com/example/forum/controller/post/api/PostTrendingController.java
import com.example.forum.dto.post.PostResponseDTO;
=======
import com.example.forum.service.post.PostService;
>>>>>>> f54d5da9bb04b87072a4f7697d5e1c2edbac7be3:src/main/java/com/example/forum/controller/post/api/TopPostController.java
import com.example.forum.service.post.community.CommunityPostService;
import com.example.forum.service.post.trending.TrendingPostService;
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
public class TopPostController implements TopPostApiDocs {

    private final AuthValidator authValidator;
    private final CommunityPostService communityPostService;
    private final TrendingPostService trendingPostService;

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getRecentPostsFromMyCommunities(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        List<PostPreviewDTO> response = communityPostService.getRecentPostsFromJoinedCommunities(username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getTopPostsThisWeek(
            @AuthenticationPrincipal UserDetails userDetails
    ) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        List<PostPreviewDTO> response = trendingPostService.getTopPostsThisWeek(username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
