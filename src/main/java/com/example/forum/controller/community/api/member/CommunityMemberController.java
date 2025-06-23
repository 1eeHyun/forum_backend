package com.example.forum.controller.community.api.member;

import com.example.forum.controller.community.docs.member.CommunityMemberApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.user.UserDTO;
import com.example.forum.service.community.member.CommunityMemberService;
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
@RequestMapping("/api/communities/{communityId}/members")
@RequiredArgsConstructor
public class CommunityMemberController implements CommunityMemberApiDocs {

    private final CommunityMemberService communityMemberService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<List<UserDTO>>> getAllCommunityMembers(
            @PathVariable Long communityId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        List<UserDTO> response = communityMemberService.getAllMembers(communityId, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<UserDTO>>> getOnlineUsers(
            @PathVariable Long communityId) {

        List<UserDTO> response = communityMemberService.getOnlineUsers(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<UserDTO>>> getNewMembers(
            @PathVariable Long communityId) {

        List<UserDTO> response = communityMemberService.getNewMembersThisWeek(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
