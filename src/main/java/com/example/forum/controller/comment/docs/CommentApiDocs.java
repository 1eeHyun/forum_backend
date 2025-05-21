package com.example.forum.controller.comment.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "Comment", description = "Comment related API")
public interface CommentApiDocs {

    @Operation(
            summary = "Create a comment",
            description = "Creates a comment on a post. If parentCommentId is provided, the comment will be treated as a reply.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found / User not found / Parent comment not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content
                    )
            }
    )
    @PostMapping
    ResponseEntity<CommonResponse<CommentResponseDTO>> create(
            @RequestBody(
                    description = "Comment creation request containing content, postId, and optionally parentCommentId",
                    required = true
            )
            CommentRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Get all comments for a post",
            description = "Retrieves all top-level comments and their nested replies for a specific post.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comments retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/{postId}")
    ResponseEntity<CommonResponse<List<CommentResponseDTO>>> getAllComments(
            @Parameter(description = "ID of the post to fetch comments for", required = true)
            @PathVariable Long postId
    );

    @Operation(
            summary = "Reply to a comment",
            description = "Creates a reply to an existing comment. Requires the parent comment ID and reply content.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Reply created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CommentResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Parent comment not found or user not found",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content
                    )
            }
    )
    @PostMapping("/reply")
    ResponseEntity<CommonResponse<CommentResponseDTO>> reply(
            @RequestBody(
                    description = "Reply request with content and parentCommentId",
                    required = true
            )
            CommentRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Delete a comment",
            description = "Deletes a comment if the authenticated user is the author.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Comment deleted successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user is not logged in or not the comment author",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Comment or user not found",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{commentId}")
    ResponseEntity<CommonResponse<Void>> delete(
            @Parameter(description = "ID of the comment to delete", required = true)
            @PathVariable Long commentId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
