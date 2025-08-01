package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostPreviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Tag(name = "Top post related API", description = "Top posts")
public interface TopPostApiDocs {

    @Operation(
            summary = "Get top posts this week",
            description = "Retrieves the top 10 posts with the highest number of likes created within the last 7 days.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Top posts fetched successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    @GetMapping("/top-weekly")
    ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getTopPostsThisWeek(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Get recent posts from joined communities",
            description = "Returns the 5 most recent posts from communities the current user has joined.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved recent posts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostPreviewDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not authenticated",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error",
                            content = @Content
                    )
            }
    )
    @GetMapping("/my-communities/recent")
    ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getRecentPostsFromMyCommunities(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );
}
