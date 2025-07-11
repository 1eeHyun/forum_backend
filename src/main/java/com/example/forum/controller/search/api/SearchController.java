package com.example.forum.controller.search.api;

import com.example.forum.controller.search.docs.SearchApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.search.SearchResponseDTO;
import com.example.forum.service.search.SearchService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController implements SearchApiDocs {

    private final SearchService searchService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<SearchResponseDTO>> search(
            @RequestParam("query") String query,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        SearchResponseDTO response = searchService.searchAll(query, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<ProfilePreviewDTO>>> searchUsers(
            @RequestParam("keyword") String keyword) {

        List<ProfilePreviewDTO> response = searchService.searchUsers(keyword);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> searchPosts(
            @RequestParam("keyword") String keyword,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        List<PostPreviewDTO> response = searchService.searchPosts(keyword, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> searchCommunities(
            @RequestParam("keyword") String keyword) {

        List<CommunityPreviewDTO> response = searchService.searchCommunities(keyword);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
