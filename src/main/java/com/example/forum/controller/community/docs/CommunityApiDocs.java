package com.example.forum.controller.community.docs;


import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.*;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.util.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Tag(name = "Community", description = "Community related API")
public interface CommunityApiDocs {

    @Operation(
            summary = "Create a new community",
            description = "Creates a new community with the provided name and description. The creator will be automatically registered as a manager.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Community created successfully. Returns the ID of the new community.",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input or duplicate community name",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    )
            }
    )
    @PostMapping
    ResponseEntity<CommonResponse<Long>> create(
            @RequestBody(
                    description = "Community creation request containing name and description",
                    required = true
            )
            CommunityRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Get communities the user is a member of",
            description = "Retrieves a list of communities the currently logged-in user has joined.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user's communities",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommunityPreviewDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not logged in",
                            content = @Content
                    )
            }
    )
    @GetMapping("/my")
    ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> getMyCommunities(
            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Retrieve a community",
            description = "Retrieves full details of a community, including all members, online users, and the current user's role if applicable.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved community details",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommunityDetailDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{communityId}")
    ResponseEntity<CommonResponse<CommunityDetailDTO>> getCommunity(
            @Parameter(description = "ID of the community to retrieve", required = true)
            @PathVariable Long communityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Get online users of a community",
            description = "Retrieves a list of users who are currently online in the specified community.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of online users",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{communityId}/online-users")
    ResponseEntity<CommonResponse<List<UserDTO>>> getOnlineUsers(
            @Parameter(description = "ID of the community to check online users", required = true)
            @PathVariable Long communityId
    );

    @Operation(
            summary = "Get categories of a community",
            description = "Retrieves all categories associated with the specified community.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Community not found")
            }
    )
    @GetMapping("/{communityId}/categories")
    ResponseEntity<CommonResponse<List<CategoryResponseDTO>>> getCategories(
            @Parameter(description = "ID of the community to retrieve categories from", required = true)
            @PathVariable Long communityId
    );

    @Operation(
            summary = "Add a new category to a community",
            description = "Adds a new category to the specified community. Only accessible by community managers.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category successfully added"),
                    @ApiResponse(responseCode = "404", description = "Community not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to manage this community")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Category information including name and optional description",
            required = true,
            content = @Content(schema = @Schema(implementation = CategoryRequestDTO.class))
    )
    @PostMapping("/{communityId}/categories")
    ResponseEntity<CommonResponse<Void>> addCategory(
            @Parameter(description = "ID of the community to which the category will be added", required = true)
            @PathVariable Long communityId,

            @Valid @RequestBody CategoryRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Join a community",
            description = "Allows the currently logged-in user to join the specified community.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully joined the community"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "User already joined or community is private/restricted"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in"
                    )
            }
    )
    @PostMapping("/{communityId}/join")
    ResponseEntity<CommonResponse<Void>> joinCommunity(
            @Parameter(description = "ID of the community to join", required = true)
            @PathVariable Long communityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Leave a community",
            description = "Allows the currently logged-in user to leave the specified community.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully left the community"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "User is not a member of the community or is the only manager"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in"
                    )
            }
    )
    @PostMapping("/{communityId}/leave")
    ResponseEntity<CommonResponse<Void>> leaveCommunity(
            @Parameter(description = "ID of the community to leave", required = true)
            @PathVariable Long communityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Posts of a community ----------------------
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
    @GetMapping("/{communityId}/posts")
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

    // ---------------------- Posts of a category of a community ----------------------
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
    @GetMapping("/{communityId}/categories/{categoryId}/posts")
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
    @GetMapping("/{communityId}/top-posts-by-category")
    ResponseEntity<Map<String, List<PostResponseDTO>>> getTopPostsByCategoryThisWeek(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "week") String period,
            @RequestParam(defaultValue = "3") int limit
    );
}
