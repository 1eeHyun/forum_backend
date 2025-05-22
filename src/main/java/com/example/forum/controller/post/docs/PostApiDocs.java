package com.example.forum.controller.post.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Post", description = "Post related API")
public interface PostApiDocs {

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
            @RequestParam(defaultValue = "10") int size
    );


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
    @GetMapping("/{id}")
    ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(
            @Parameter(description = "ID of the post to retrieve", required = true)
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


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
    @PutMapping("/{id}")
    ResponseEntity<CommonResponse<PostResponseDTO>> update(
            @Parameter(description = "ID of the post to update", required = true)
            @PathVariable Long id,

            @RequestBody(
                    description = "Post update data (title, content, etc.)",
                    required = true
            )
            PostRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


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
    @DeleteMapping("/{id}")
    ResponseEntity<CommonResponse<Void>> delete(
            @Parameter(description = "ID of the post to delete", required = true)
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


    @Operation(
            summary = "Toggle like on a post",
            description = "Toggles a like on the given post. If the user has already liked it, the like will be removed.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Post like toggled successfully",
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
    @PostMapping("/{id}/likes")
    ResponseEntity<CommonResponse<Void>> likePost(
            @Parameter(description = "ID of the post to like or unlike", required = true)
            @PathVariable Long id,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );


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
    @GetMapping("/{id}/likes")
    ResponseEntity<CommonResponse<Long>> getLikesCount(
            @Parameter(description = "ID of the post to retrieve like count for", required = true)
            @PathVariable Long id
    );


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
    @GetMapping("/{id}/likes/users")
    ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(
            @Parameter(description = "ID of the post", required = true)
            @PathVariable Long id
    );


    @Operation(
            summary = "Upload post image",
            description = "Uploads an image file for a post and returns the URL of the stored image.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Image file to upload (multipart/form-data)",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Image uploaded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = String.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file or upload failed",
                            content = @Content
                    )
            }
    )
    @PostMapping(value = "/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<CommonResponse<String>> uploadPostImage(
            @Parameter(
                    description = "The image file to upload",
                    required = true,
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestParam("file") MultipartFile file
    );


}
