package com.example.forum.controller.post.api;

import com.example.forum.common.SortOrder;
import com.example.forum.controller.post.docs.PostApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostCreateRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
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

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController implements PostApiDocs {

    private final PostService postService;
    private final AuthValidator authValidator;

    @Override
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getPosts(
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        SortOrder sortOrder = SortOrder.from(sort);
        List<PostResponseDTO> posts = postService.getPagedPosts(sortOrder, page, size, username);

        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    public ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = (userDetails == null) ? null : authValidator.extractUsername(userDetails);

        PostDetailDTO response = postService.getPostDetail(postId, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    public ResponseEntity<CommonResponse<PostResponseDTO>> create(
            @Valid @RequestBody PostCreateRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        PostResponseDTO post = postService.createPost(dto, username);
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    public ResponseEntity<CommonResponse<PostResponseDTO>> update(
            @PathVariable Long postId,
            @Valid @RequestBody PostCreateRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        PostResponseDTO post = postService.updatePost(postId, dto, username);
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long postId,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        postService.deletePost(postId, username);

        return ResponseEntity.ok(CommonResponse.success(null));
    }
}
