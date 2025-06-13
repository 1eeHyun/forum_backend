package com.example.forum.controller.community.docs;


import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
            description = "Retrieves full details of a community, including all online users, and the current user's role if applicable.",
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
            summary = "Update community profile image",
            description = "Updates the profile image of a specific community. Only the manager of the community can perform this action.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Multipart form data including the image file and optional position (X, Y)",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Profile image updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid file or coordinates"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Not a community manager"),
                    @ApiResponse(responseCode = "404", description = "Community not found")
            }
    )
    @PutMapping(value = "/{communityId}/profile-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<Void>> updateCommunityProfileImage(
            @Parameter(description = "ID of the community", required = true)
            @PathVariable Long communityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Profile image file", required = true)
            @RequestParam MultipartFile image,

            @Parameter(description = "X position (0.0 ~ 1.0)", example = "0.5")
            @RequestParam(required = false) Double positionX,

            @Parameter(description = "Y position (0.0 ~ 1.0)", example = "0.5")
            @RequestParam(required = false) Double positionY
    );

    @Operation(
            summary = "Update community banner image",
            description = "Updates the banner image of a specific community. Only the manager of the community can perform this action.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "Multipart form data including the banner image file",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Banner image updated successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid file"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "403", description = "Forbidden – Not a community manager"),
                    @ApiResponse(responseCode = "404", description = "Community not found")
            }
    )
    @PutMapping(value = "/{communityId}/banner-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<Void>> updateCommunityBannerImage(
            @Parameter(description = "ID of the community", required = true)
            @PathVariable Long communityId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "Banner image file", required = true)
            @RequestParam MultipartFile image
    );
}
