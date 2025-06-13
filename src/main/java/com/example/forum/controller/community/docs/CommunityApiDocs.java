package com.example.forum.controller.community.docs;


import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.util.UserDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

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

    @Operation(
            summary = "Get new members of a community this week",
            description = "Returns a list of users who joined the specified community within the past 7 days.",
            parameters = {
                    @Parameter(
                            name = "communityId",
                            description = "ID of the community",
                            required = true,
                            in = ParameterIn.PATH
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of new members",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Community not found"
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error"
                    )
            }
    )
    @GetMapping("/{communityId}/new-members")
    ResponseEntity<CommonResponse<List<UserDTO>>> getNewMembers(
            @PathVariable Long communityId
    );
}
