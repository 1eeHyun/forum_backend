package com.example.forum.controller.comment.docs;

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

@Tag(name = "Comment Like/Dislike", description = "API related to liking and disliking comments")
public interface CommentLikeAPIDocs {

    @Operation(
            summary = "Toggle like on a comment",
            description = "Toggles a like for the given comment. If the user already liked the comment, the like will be removed. Also removes any existing dislike.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Like toggled successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or user not found",
                            content = @Content
                    )
            }
    )
    @PostMapping("/likes")
    ResponseEntity<CommonResponse<Void>> likeComment(
            @Parameter(description = "ID of the comment to like or unlike", required = true)
            @PathVariable Long commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Toggle dislike on a comment",
            description = "Toggles a dislike for the given comment. If the user already disliked the comment, it will be removed. Also removes any existing like.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Dislike toggled successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or user not found",
                            content = @Content
                    )
            }
    )
    ResponseEntity<CommonResponse<Void>> dislikeComment(
            @Parameter(description = "ID of the comment to dislike or remove dislike", required = true)
            @PathVariable Long commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Get like count for a comment",
            description = "Returns the total number of likes for the given comment ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved like count",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/likes/count")
    ResponseEntity<CommonResponse<Long>> countLikes(
            @Parameter(description = "ID of the comment", required = true)
            @PathVariable Long commentId
    );


    @Operation(
            summary = "Get dislike count for a comment",
            description = "Returns the total number of dislikes for the given comment ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved dislike count",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/dislikes/count")
    ResponseEntity<CommonResponse<Long>> countDislikes(
            @Parameter(description = "ID of the comment", required = true)
            @PathVariable Long commentId
    );


    @Operation(
            summary = "Check if the current user liked the comment",
            description = "Returns true if the authenticated user has liked the specified comment. Returns false if not liked or not logged in.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully checked like status",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or user not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/likes/me")
    ResponseEntity<CommonResponse<Boolean>> hasUserLiked(
            @Parameter(description = "ID of the comment to check like status", required = true)
            @PathVariable Long commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Check if the current user disliked the comment",
            description = "Returns true if the authenticated user has disliked the specified comment. Returns false if not disliked or not logged in.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully checked dislike status",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Boolean.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or user not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/dislikes/me")
    ResponseEntity<CommonResponse<Boolean>> hasUserDisliked(
            @Parameter(description = "ID of the comment to check dislike status", required = true)
            @PathVariable Long commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

}
