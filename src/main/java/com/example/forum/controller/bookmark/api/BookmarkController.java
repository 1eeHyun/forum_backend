package com.example.forum.controller.bookmark.api;

import com.example.forum.controller.bookmark.docs.BookmarkApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.service.bookmark.BookmarkService;
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
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
public class BookmarkController implements BookmarkApiDocs {

    private final BookmarkService bookmarkService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<Void>> toggleBookmark(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        bookmarkService.toggleBookmark(postId, username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getBookmarkedPosts(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);
        List<PostPreviewDTO> response = bookmarkService.getBookmarkedPosts(username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<Boolean>> isPostBookmarked(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        boolean response = bookmarkService.isBookmarked(postId, username);

        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
