package com.example.forum.controller.profile.api.community;

import com.example.forum.controller.profile.docs.community.ProfileCommunityApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.service.community.CommunityService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/profiles/{username}/communities")
@RequiredArgsConstructor
public class ProfileCommunityController implements ProfileCommunityApiDocs {

    private final AuthValidator authValidator;
    private final CommunityService communityService;

    @Override
    public ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> getJoinedCommunities(
            @PathVariable String username,
            @AuthenticationPrincipal UserDetails userDetails) {

        String currentUsername = (userDetails != null) ? authValidator.extractUsername(userDetails) : null;

        List<CommunityPreviewDTO> response = communityService.getJoinedCommunities(username, currentUsername);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
