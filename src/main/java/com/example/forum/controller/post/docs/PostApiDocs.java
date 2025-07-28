package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Post", description = "Post related API")
public interface PostApiDocs {

    // ---------------------- Posts for Home ----------------------
    @Operation(
            summary = "Get paginated posts",
            description = "Retrieves a list of posts with optional sorting and pagination.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved posts",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid sort value or pagination input",
                            content = @Content
                    )
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getPosts(
            @Parameter(description = "Sort order: top, newest, or oldest", example = "newest")
            @RequestParam(defaultValue = "newest") String sort,

            @Parameter(description = "Page number (zero-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "Number of posts per page", example = "10")
            @RequestParam(defaultValue = "10") int size,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Posts for Detail ----------------------
    @Operation(
            summary = "Get post detail",
            description = "Retrieves the detailed content of a post. If the user is logged in, additional information such as like status may be included.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post detail retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostDetailDTO.class)
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
    ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(
            @Parameter(description = "ID of the post to retrieve", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Create a new Post ----------------------
    @Operation(
            summary = "Create a new post",
            description = "Creates a new post for the authenticated user and returns the post preview data.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
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
    ResponseEntity<CommonResponse<PostResponseDTO>> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Post request body containing title, content, etc.",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody
            @Valid
            PostRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Update for an existing post ----------------------
    @Operation(
            summary = "Update a post",
            description = "Updates the content of a post. Only the author of the post can perform this action.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post updated successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PostResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request body",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - not the author of the post",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @PutMapping("/{postId}")
    ResponseEntity<CommonResponse<PostResponseDTO>> update(
            @Parameter(description = "ID of the post to update", required = true)
            @PathVariable Long postId,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Post update data (title, content, etc.)",
                    required = true
            )
            @org.springframework.web.bind.annotation.RequestBody
            PostRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );

    // ---------------------- Delete for an existing post ----------------------
    @Operation(
            summary = "Delete a post",
            description = "Deletes a post by ID. Only the author of the post can delete it.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post deleted successfully",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - user not logged in",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - user is not the author of the post",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Post not found",
                            content = @Content
                    )
            }
    )
    @DeleteMapping("/{postId}")
    ResponseEntity<CommonResponse<Void>> delete(
            @Parameter(description = "ID of the post to delete", required = true)
            @PathVariable Long postId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
