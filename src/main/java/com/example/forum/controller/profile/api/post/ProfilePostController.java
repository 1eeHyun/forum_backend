package com.example.forum.controller.profile.api.post;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.profile.docs.post.ProfilePostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.post.profile.ProfilePostService;
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

@RestController
@RequestMapping("/api/profiles/{username}/posts")
@RequiredArgsConstructor
public class ProfilePostController implements ProfilePostApiDocs {

    private final AuthValidator authValidator;
    private final ProfilePostService profilePostService;

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getProfilePosts(
            @PathVariable String username,
            @RequestParam String sort,
            @RequestParam int page,
            @RequestParam int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        SortOrder sortOrder = SortOrder.from(sort);
        String myUsername = authValidator.extractUsername(userDetails);
        List<PostResponseDTO> posts = profilePostService.getProfilePosts(username, myUsername, sortOrder, page, size);

        return ResponseEntity.ok(CommonResponse.success(posts));
    }
}
