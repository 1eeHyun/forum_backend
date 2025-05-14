package com.example.forum.controller.comment;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.comment.CommentRequestDTO;
import com.example.forum.dto.comment.CommentResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@Tag(name = "Comment", description = "Comment related API")
public interface CommentApiDocs {

    @Operation(
            summary = "Create a new comment",
            description = "Create a new comment to an existing post. The comment will be a parent comment for future replies."
    )
    ResponseEntity<CommonResponse<CommentResponseDTO>> create(CommentRequestDTO dto, UserDetails userDetails);

    @Operation(
            summary = "Get every comment of a post",
            description = "Retrieve every comment of an existing post including replies."
    )
    ResponseEntity<CommonResponse<List<CommentResponseDTO>>> getAllComments(Long postId);

    @Operation(
            summary = "Create a new reply",
            description = "Create a new comment to an existing parent comment."
    )
    ResponseEntity<CommonResponse<CommentResponseDTO>> reply(CommentRequestDTO dto, UserDetails userDetails);

    @Operation(
            summary = "Delete an existing comment",
            description = "Deletes an existing comment, only the author can do it."
    )
    ResponseEntity<CommonResponse<Void>> delete(Long commentId, UserDetails userDetails);
}
