package com.example.forum.controller.post;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "Post", description = "Post related API")
public interface PostApiDocs {

    @Operation(
            summary = "Retrieve every post",
            description = "Retrieve every post with ascending order."
    )
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllPublicPostAsc();

    @Operation(
            summary = "Retrieve every post",
            description = "Retrieve every post with descending order."
    )
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllPublicPostDesc();

    @Operation(
            summary = "Retrieve a post",
            description = "Retrieves an existing post detail information"
    )
    ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(Long postId, UserDetails userDetails);

    @Operation(
            summary = "Create a new post",
            description = "Creates a new post only logged-in user can do it."
    )
    ResponseEntity<CommonResponse<PostResponseDTO>> create(PostRequestDTO dto, UserDetails userDetails);

    @Operation(
            summary = "Update an existing post",
            description = "Update an existing post only author can do it."
    )
    ResponseEntity<CommonResponse<PostResponseDTO>> update(Long id, PostRequestDTO dto, UserDetails userDetails);

    @Operation(
            summary = "Delete an existing post",
            description = "Delete an existing post only author can do it."
    )
    ResponseEntity<CommonResponse<Void>> delete(Long id, UserDetails userDetails);

    @Operation(
            summary = "Like button of a post",
            description = "Handles pressing like button of an existing post, only logged-in user can do it."
    )
    ResponseEntity<CommonResponse<Void>> likePost(Long id, UserDetails userDetails);

    @Operation(
            summary = "Get likes count",
            description = "Retrieves likes count of an existing post."
    )
    ResponseEntity<CommonResponse<Long>> getLikesCount(Long id);

    @Operation(
            summary = "Get who likes this post",
            description = "Retrieves all users who like an existing post, showing nickname, profile image."
    )
    ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(Long id);

    @Operation(
            summary = "Upload a post image",
            description = "Uploads a single image for a post and returns the image URL."
    )
    ResponseEntity<CommonResponse<String>> uploadPostImage(MultipartFile file);

}
