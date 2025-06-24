package com.example.forum.controller.profile.docs.community;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityPreviewDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Profile community", description = "Profile community related API")
public interface ProfileCommunityApiDocs {

    @Operation(
            summary = "Get joined communities by user profile",
            description = "Retrieves the list of communities the specified user has joined. If the requester is the same as the target user, all joined communities are shown. Otherwise, only publicly visible memberships may be shown depending on policy.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Joined communities retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CommunityPreviewDTO.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content
                    )
            }
    )
    @GetMapping
    public ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> getJoinedCommunities(
            @Parameter(description = "Username of the target profile", required = true)
            @PathVariable String username,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
