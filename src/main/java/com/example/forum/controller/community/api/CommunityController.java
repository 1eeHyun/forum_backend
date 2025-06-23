package com.example.forum.controller.community.api;

import com.example.forum.controller.community.docs.CommunityApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.image.ImageUploadRequestDTO;
import com.example.forum.service.community.CommunityService;
import com.example.forum.service.community.manage.CommunityManageService;
import com.example.forum.service.community.member.CommunityMemberService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
@Slf4j
public class CommunityController implements CommunityApiDocs {

    private final CommunityService communityService;
    private final CommunityMemberService communityMemberService;
    private final CommunityManageService communityManageService;
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
        communityMemberService.addMember(communityId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> leaveCommunity(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityMemberService.leaveCommunity(communityId, username);

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

        String username = null;
        if (userDetails != null)
            username = authValidator.extractUsername(userDetails);

        CommunityDetailDTO response = communityService.getCommunityDetail(communityId, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> updateCommunityProfileImage(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam MultipartFile image,
            @RequestParam(required = false) Double positionX,
            @RequestParam(required = false) Double positionY) {

        String username = authValidator.extractUsername(userDetails);

        ImageUploadRequestDTO dto = new ImageUploadRequestDTO();
        dto.setImage(image);
        dto.setPositionX(positionX);
        dto.setPositionY(positionY);

        communityManageService.updateProfileImage(username, communityId, dto);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> updateCommunityBannerImage(
            Long communityId,
            UserDetails userDetails,
            MultipartFile image) {

        String username = authValidator.extractUsername(userDetails);

        ImageUploadRequestDTO dto = new ImageUploadRequestDTO();
        dto.setImage(image);

        communityManageService.updateBannerImage(username, communityId, dto);

        return ResponseEntity.ok(CommonResponse.success());
    }
}
