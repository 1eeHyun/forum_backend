package com.example.forum.controller.community.api.post;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.community.docs.post.CommunityPostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.post.community.CommunityPostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/communities/{communityId}")
@RequiredArgsConstructor
public class CommunityPostController implements CommunityPostApiDocs {

    private final CommunityPostService communityPostService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getCommunityPosts(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        SortOrder sortOrder = SortOrder.from(sort);
        List<PostResponseDTO> response = communityPostService.getCommunityPosts(communityId, sortOrder, page, size, category, username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Map<String, List<PostResponseDTO>>>> getTopPostsByCategoryThisWeek(
            @PathVariable Long communityId,
            @RequestParam(defaultValue = "3") int limit,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        Map<String, List<PostResponseDTO>> response = communityPostService.getTopPostsThisWeekByCategories(communityId, limit, username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
