package com.example.forum.controller.community.api;

import com.example.forum.controller.community.docs.CommunityRuleApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityRuleRequestDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;
import com.example.forum.service.community.manage.CommunityManageService;
import com.example.forum.validator.auth.AuthValidator;
import jakarta.validation.Valid;
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
@RequestMapping("/api/communities/{communityId}/rules")
@RequiredArgsConstructor
@Slf4j
public class CommunityRuleController implements CommunityRuleApiDocs {

    private final CommunityManageService communityManageService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<Void>> addRule(
            @PathVariable Long communityId,
            @Valid @RequestBody CommunityRuleRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityManageService.addRule(communityId, request, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> updateRule(
            @PathVariable Long communityId,
            @PathVariable Long ruleId,
            @RequestBody CommunityRuleRequestDTO request,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityManageService.updateRule(communityId, ruleId, request, username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> deleteRule(
            @PathVariable Long communityId,
            @PathVariable Long ruleId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        communityManageService.deleteRule(communityId, ruleId, username);

        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<List<CommunityRuleResponseDTO>>> getRulesByCommunity(
            @PathVariable Long communityId) {

        List<CommunityRuleResponseDTO> response = communityManageService.getRules(communityId);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
