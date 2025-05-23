package com.example.forum.controller.post.api;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.post.docs.PostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.like.post.PostLikeService;
import com.example.forum.service.post.PostService;
import com.example.forum.validator.auth.AuthValidator;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController implements PostApiDocs {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getPosts(
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        SortOrder sortOrder = SortOrder.from(sort);
        List<PostResponseDTO> posts = postService.getPagedPosts(sortOrder, page, size);
        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    public ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {


        String username = userDetails == null ? null : authValidator.extractUsername(userDetails);

        PostDetailDTO response = postService.getPostDetail(id, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<PostResponseDTO>> create(
            @Valid @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        PostResponseDTO post = postService.createPost(dto, username);
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    public ResponseEntity<CommonResponse<PostResponseDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        PostResponseDTO post = postService.updatePost(id, dto, username);
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        postService.deletePost(id, username);

        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        postLikeService.toggleLike(id, username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    public ResponseEntity<CommonResponse<Long>> getLikesCount(
            @PathVariable Long id) {

        long response = postLikeService.countLikes(id);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(
            @PathVariable Long id) {

        List<LikeUserDTO> response = postLikeService.getLikeUsers(id);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<String>> uploadPostImage(
            @RequestParam("file") MultipartFile file) {

        String response = postService.uploadImage(file);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getRecentPostsFromMyCommunities(
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = null;
        if (userDetails != null)
             username = authValidator.extractUsername(userDetails);

        List<PostPreviewDTO> response = postService.getRecentPostsFromJoinedCommunities(username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getTopPostsThisWeek() {

        List<PostPreviewDTO> topPosts = postService.getTopPostsThisWeek();
        return ResponseEntity.ok(CommonResponse.success(topPosts));
    }

    @Override
    public ResponseEntity<CommonResponse<List<PostPreviewDTO>>> getRecentlyViewedPosts(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) List<Long> localIds) {


        if (userDetails != null) {
            String username = authValidator.extractUsername(userDetails);
            List<PostPreviewDTO> response = postService.getRecentlyViewedPosts(username);

            return ResponseEntity.ok(CommonResponse.success(response));
        }

        if (localIds != null && !localIds.isEmpty()) {
            return ResponseEntity.ok(CommonResponse.success(
                    postService.getPreviewPostsByIds(localIds)
            ));
        }

        return ResponseEntity.ok(CommonResponse.success(Collections.emptyList()));
    }
}
