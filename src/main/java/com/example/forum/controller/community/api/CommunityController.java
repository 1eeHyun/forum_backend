package com.example.forum.controller.community.api;

import com.example.forum.controller.community.docs.CommunityApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.*;
import com.example.forum.dto.util.OnlineUserDTO;
import com.example.forum.service.community.CommunityService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
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
    public ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> getMyCommunities(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        List<CommunityPreviewDTO> response = communityService.getMyCommunities(username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<CommunityDetailDTO>> getCommunity(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        CommunityDetailDTO response = communityService.getCommunityDetail(id, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<OnlineUserDTO>>> getOnlineUsers(
            @PathVariable Long id) {

        List<OnlineUserDTO> response = communityService.getOnlineUsers(id);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<CategoryResponseDTO>>> getCategories(
            @PathVariable Long communityId) {

        List<CategoryResponseDTO> response = communityService.getCategories(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> addCategory(
            @PathVariable Long communityId,
            @RequestBody CategoryRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityService.addCategory(communityId, dto, username);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
