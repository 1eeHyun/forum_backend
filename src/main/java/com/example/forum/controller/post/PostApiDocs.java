package com.example.forum.controller.post;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "Post", description = "Post related API")
public interface PostApiDocs {

    @Operation(
            summary = "Retrieve every post",
            description = "Retrieve every post with ascending order."
    )
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllPublicPostAsc(UserDetails userDetails);

    @Operation(
            summary = "Retrieve every post",
            description = "Retrieve every post with descending order."
    )
    ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllPublicPostDesc(UserDetails userDetails);

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
}
