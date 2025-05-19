package com.example.forum.controller.comment;

import com.example.forum.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Comment Like/Dislike", description = "API related to liking and disliking comments")
public interface CommentLikeAPIDocs {

    @Operation(
            summary = "Toggle like on a comment",
            description = "Toggles like on a comment. If already liked, it will remove the like. Only available to logged-in users."
    )
    ResponseEntity<CommonResponse<Void>> likeComment(Long commentId, UserDetails userDetails);

    @Operation(
            summary = "Toggle dislike on a comment",
            description = "Toggles dislike on a comment. If already disliked, it will remove the dislike. Only available to logged-in users."
    )
    ResponseEntity<CommonResponse<Void>> dislikeComment(Long commentId, UserDetails userDetails);

    @Operation(
            summary = "Get like count of a comment",
            description = "Returns the number of likes a comment has."
    )
    ResponseEntity<CommonResponse<Long>> countLikes(Long commentId);

    @Operation(
            summary = "Get dislike count of a comment",
            description = "Returns the number of dislikes a comment has."
    )
    ResponseEntity<CommonResponse<Long>> countDislikes(Long commentId);

    @Operation(
            summary = "Check if current user liked a comment",
            description = "Checks whether the currently logged-in user has liked the specified comment."
    )
    ResponseEntity<CommonResponse<Boolean>> hasUserLiked(Long commentId, UserDetails userDetails);

    @Operation(
            summary = "Check if current user disliked a comment",
            description = "Checks whether the currently logged-in user has disliked the specified comment."
    )
    ResponseEntity<CommonResponse<Boolean>> hasUserDisliked(Long commentId, UserDetails userDetails);
}
