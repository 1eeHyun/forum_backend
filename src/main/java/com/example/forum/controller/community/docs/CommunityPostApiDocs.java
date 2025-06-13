package com.example.forum.controller.community.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Tag(name = "Community Post", description = "Community Posts related API")
public interface CommunityPostApiDocs {

    @Operation(
            summary = "Get posts in a community",
            description = "Returns paginated posts in a community. Supports sorting by 'top', 'newest', or 'oldest'.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved posts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/posts")
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getCommunityPosts(
            @Parameter(description = "ID of the community", required = true)
            @PathVariable Long communityId,

            @Parameter(description = "Sort order: top, newest, or oldest", example = "newest")
            @RequestParam(defaultValue = "newest") String sort,

            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of posts per page", example = "5")
            @RequestParam(defaultValue = "5") int size
    );

    @Operation(
            summary = "Get posts in a specific category within a community",
            description = "Returns paginated posts filtered by category in a community. Supports sorting.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved posts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community or Category not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/categories/{categoryId}/posts")
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getCommunityCategoryPosts(
            @Parameter(description = "ID of the community", required = true)
            @PathVariable Long communityId,

            @Parameter(description = "ID of the category", required = true)
            @PathVariable Long categoryId,

            @Parameter(description = "Sort order: top, newest, or oldest", example = "newest")
            @RequestParam(defaultValue = "newest") String sort,

            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of posts per page", example = "5")
            @RequestParam(defaultValue = "5") int size
    );

    @Operation(
            summary = "Get top posts by category in a community",
            description = "Returns top posts for each category in the community, within a given period such as 'week' or 'month'. Limited to a certain number per category.",
            parameters = {
                    @Parameter(name = "communityId", description = "ID of the community", required = true, in = ParameterIn.PATH),
                    @Parameter(name = "period", description = "Time range (e.g., 'week', 'month')", in = ParameterIn.QUERY),
                    @Parameter(name = "limit", description = "Maximum number of top posts per category", in = ParameterIn.QUERY)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Top posts grouped by category",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class))),
                    @ApiResponse(responseCode = "404", description = "Community not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/top-posts-by-category")
    ResponseEntity<CommonResponse<Map<String, List<PostResponseDTO>>>> getTopPostsByCategoryThisWeek(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "3") int limit
    );
}
