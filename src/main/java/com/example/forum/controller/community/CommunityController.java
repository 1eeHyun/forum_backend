package com.example.forum.controller.community;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.community.CommunityResponseDTO;
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
    @PostMapping
    public ResponseEntity<CommonResponse<CommunityResponseDTO>> create(
            @RequestBody CommunityRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        CommunityResponseDTO response = communityService.create(dto, username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/my")
    public ResponseEntity<CommonResponse<List<CommunityResponseDTO>>> getMyCommunities(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        List<CommunityResponseDTO> response = communityService.getMyCommunities(username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<CommunityResponseDTO>> getCommunity(@PathVariable Long id) {

        CommunityResponseDTO response = communityService.getCommunity(id);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
