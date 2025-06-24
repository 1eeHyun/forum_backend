package com.example.forum.controller.profile.docs.post;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
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
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Profile post", description = "Profile post related API")
public interface ProfilePostApiDocs {

    @Operation(
            summary = "Get posts by user profile",
            description = "Retrieves a paginated and sorted list of posts for the specified user. If the logged-in user is the same as the profile owner, all posts are shown. Otherwise, only public or shared-community posts are shown.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Posts retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
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
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getProfilePosts(
            @Parameter(description = "Username of the profile", required = true)
            @PathVariable String username,

            @Parameter(description = "Sort order: top, newest, or oldest", required = true, example = "newest")
            @RequestParam String sort,

            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam int page,

            @Parameter(description = "Number of posts per page", example = "10")
            @RequestParam int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
