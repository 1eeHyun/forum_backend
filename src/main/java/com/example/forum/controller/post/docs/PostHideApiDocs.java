package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Tag(name = "Post Hide/Unhide toggle", description = "Hide / Unhide")
public interface PostHideApiDocs {

    @Operation(
            summary = "Toggle hide/unhide a post",
            description = "Hides the given post for the current user. If the post is already hidden, it will be unhidden.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Post hidden/unhidden successfully"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized - user not logged in"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @PostMapping("/{postId}/hide-toggle")
    ResponseEntity<CommonResponse<Void>> toggleHidePost(
            @Parameter(description = "ID of the post to hide/unhide", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
