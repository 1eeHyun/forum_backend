package com.example.forum.controller.follow.docs;

import com.example.forum.dto.CommonResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Follow", description = "Follow related API")
public interface FollowApiDocs {

    @Operation(
            summary = "Toggle follow",
            description = "Follows the specified user if not already followed. Unfollows if already following.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Follow state toggled successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Target user not found",
                            content = @Content
                    )
            }
    )
    @PostMapping("/{targetUsername}")
    ResponseEntity<CommonResponse<Void>> followToggle(
            @Parameter(description = "Username of the user to follow or unfollow", required = true)
            @PathVariable String targetUsername,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Check follow status",
            description = "Checks whether the authenticated user is following the specified user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully checked follow status",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Target user not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{targetUsername}/is-following")
    ResponseEntity<CommonResponse<Boolean>> isFollowing(
            @Parameter(description = "Username of the user to check", required = true)
            @PathVariable String targetUsername,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

}
