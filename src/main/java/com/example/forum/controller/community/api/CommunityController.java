package com.example.forum.controller.community.api;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.community.docs.CommunityApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.*;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.util.UserDTO;
import com.example.forum.service.community.CommunityService;
import com.example.forum.service.post.PostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities")
@RequiredArgsConstructor
public class CommunityController implements CommunityApiDocs {

    private final CommunityService communityService;
    private final AuthValidator authValidator;
    private final PostService postService;

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

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getCommunityPosts(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        SortOrder sortOrder = SortOrder.from(sort);
        List<PostResponseDTO> response = postService.getCommunityPosts(communityId, sortOrder, page, size);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getCommunityCategoryPosts(
            @PathVariable("communityId") Long communityId,
            @PathVariable("categoryId") Long categoryId,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        SortOrder sortOrder = SortOrder.from(sort);
        List<PostResponseDTO> response = postService.getCommunityCategoryPosts(communityId, categoryId, sortOrder, page, size);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Map<String, List<PostResponseDTO>>>> getTopPostsByCategoryThisWeek(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "3") int limit) {

        Map<String, List<PostResponseDTO>> response = postService.getTopPostsThisWeekByCategories(communityId, limit);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<UserDTO>>> getNewMembers(
            @PathVariable Long communityId) {

        List<UserDTO> response = communityService.getNewMembersThisWeek(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
