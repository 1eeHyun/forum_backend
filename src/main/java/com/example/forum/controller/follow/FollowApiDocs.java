package com.example.forum.controller.follow;

import com.example.forum.dto.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;

@Tag(name = "Follow", description = "Follow related API")
public interface FollowApiDocs {


    @Operation(
            summary = "Follow or unfollow a user",
            description = "Authenticated user follows or user unfollows the target user."
    )
    ResponseEntity<CommonResponse<Void>> followToggle(String targetUsername, UserDetails userDetails);

    @Operation(
            summary = "Check follow status",
            description = "Check if the authenticated user is following the target user."
    )
    ResponseEntity<CommonResponse<Boolean>> isFollowing(String targetUsername, UserDetails userDetails);
}
