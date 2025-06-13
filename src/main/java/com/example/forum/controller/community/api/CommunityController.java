package com.example.forum.controller.community.api;

import com.example.forum.controller.community.docs.CommunityApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.*;
import com.example.forum.dto.util.UserDTO;
import com.example.forum.service.community.CommunityService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@Slf4j
public class CommunityController implements CommunityApiDocs {

    private final CommunityService communityService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<Long>> create(
            @RequestBody CommunityRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        Long response = communityService.create(dto, username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> joinCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityService.addMember(communityId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> leaveCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityService.removeMember(communityId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> getMyCommunities(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        List<CommunityPreviewDTO> response = communityService.getMyCommunities(username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<CommunityDetailDTO>> getCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        CommunityDetailDTO response = communityService.getCommunityDetail(communityId, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<UserDTO>>> getOnlineUsers(
            @PathVariable Long communityId) {

        List<UserDTO> response = communityService.getOnlineUsers(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<UserDTO>>> getNewMembers(
            @PathVariable Long communityId) {

        List<UserDTO> response = communityService.getNewMembersThisWeek(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
