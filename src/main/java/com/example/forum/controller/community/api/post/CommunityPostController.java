package com.example.forum.controller.community.api.post;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.community.docs.post.CommunityPostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final PostService postService;

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
}
