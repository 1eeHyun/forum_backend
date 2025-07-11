package com.example.forum.controller.bookmark.docs;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "Bookmarks", description = "Bookmarks related API")
public interface BookmarkApiDocs {

    // ---------------------- Toggle bookmark ----------------------
    @Operation(
            summary = "Toggle bookmark on a post",
            description = "Bookmarks the post if not already bookmarked, or removes the bookmark if it is.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Bookmark toggled successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @PostMapping("/{postId}")
    ResponseEntity<CommonResponse<Void>> toggleBookmark(
            @Parameter(description = "ID of the post to bookmark or unbookmark", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Get bookmarked posts ----------------------
    @Operation(
            summary = "Get bookmarked posts",
            description = "Returns a list of posts bookmarked by the authenticated user.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved bookmarked posts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostPreviewDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    )
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getBookmarkedPosts(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Check if a post is bookmarked",
            description = "Returns true if the user has bookmarked the post, false otherwise.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Bookmark status retrieved successfully",
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
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{postId}/check")
    ResponseEntity<CommonResponse<Boolean>> isPostBookmarked(
            @Parameter(description = "ID of the post to check bookmark status", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
