package com.example.forum.controller.community.api;

import com.example.forum.controller.community.docs.CommunityCategoryApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CategoryResponseDTO;
import com.example.forum.service.community.CommunityService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/communities/{communityId}/categories")
@RequiredArgsConstructor
public class CommunityCategoryController implements CommunityCategoryApiDocs {

    private final CommunityService communityService;
    private final AuthValidator authValidator;

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
