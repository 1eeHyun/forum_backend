package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
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

@Tag(name = "Post reaction", description = "Post reaction related API")
public interface PostReactionApiDocs {

    @Operation(
            summary = "Like on a post",
            description = "Like on the given post. If the user has already liked it, the like will be removed.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post like successfully",
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
    @PostMapping("/likes")
    ResponseEntity<CommonResponse<Void>> likePost(
            @Parameter(description = "ID of the post to like", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Dislike on a post",
            description = "Dislike on the given post. If the user has already disliked it, the dislike will be removed.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post like successfully",
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
    @PostMapping("/dislikes")
    ResponseEntity<CommonResponse<Void>> dislikePost(
            @Parameter(description = "ID of the post to like", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Get number of like of an existing post ----------------------
    @Operation(
            summary = "Get like count of a post",
            description = "Returns the total number of likes for the given post.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Like count retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Long.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/likes")
    ResponseEntity<CommonResponse<Long>> getLikesCount(
            @Parameter(description = "ID of the post to retrieve like count for", required = true)
            @PathVariable Long postId
    );

    @Operation(
            summary = "Get my reaction to a post",
            description = "Returns the reaction type (LIKE, DISLIKE, or null)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Success"),
                    @ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @ApiResponse(responseCode = "404", description = "Post not found")
            }
    )
    @GetMapping("/reaction/me")
    ResponseEntity<CommonResponse<String>> getMyReaction(
            @Parameter(description = "ID of the post", required = true)
            @PathVariable Long postId,
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Who likes an existing Post ----------------------
    @Operation(
            summary = "Get users who liked a post",
            description = "Returns a list of users who liked the given post.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of users who liked the post",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LikeUserDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @GetMapping("/likes/users")
    ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(
            @Parameter(description = "ID of the post", required = true)
            @PathVariable Long postId
    );
}
