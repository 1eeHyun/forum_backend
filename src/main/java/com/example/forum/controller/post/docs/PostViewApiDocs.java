package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostPreviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Post Viewed", description = "Viewed posts")
public interface PostViewApiDocs {

    @Operation(
            summary = "Get recently viewed posts",
            description = "Retrieves the list of posts the user has recently viewed. "
                    + "If logged in, views are fetched from Redis. "
                    + "If not logged in, postIds must be passed via query parameters.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recently viewed posts fetched successfully")
            }
    )
    @GetMapping("/recent")
    ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getRecentlyViewedPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) List<Long> localIds
    );
}
