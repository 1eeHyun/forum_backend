package com.example.forum.controller.trending.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Trending posts related API", description = "Trend posts")
public interface TrendingPostApiDocs {

    @Operation(
            summary = "Get trending posts",
            description = "Retrieves posts ordered by trending score (likes, comments, views) within the past day.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Trending posts fetched successfully"),
                    @ApiResponse(responseCode = "400", description = "Bad request")
            }
    )
    @GetMapping("/trending")
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getTrendingPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );
}
