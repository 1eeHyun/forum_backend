package com.example.forum.controller.follow;

import com.example.forum.dto.CommonResponse;
import com.example.forum.service.follow.FollowService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/follows")
public class FollowController implements FollowApiDocs{

    private final FollowService followService;
    private final AuthValidator authValidator;

    @Override
    @PostMapping("/{userId}")
    public ResponseEntity<CommonResponse<Void>> follow(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        followService.follow(targetUsername, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @DeleteMapping("/{userId}")
    public ResponseEntity<CommonResponse<Void>> unfollow(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        followService.unfollow(targetUsername, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<CommonResponse<Boolean>> isFollowing(
            @PathVariable String targetUsername,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        boolean response = followService.isFollowing(targetUsername, username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
