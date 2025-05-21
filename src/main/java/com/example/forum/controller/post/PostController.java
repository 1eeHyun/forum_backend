package com.example.forum.controller.post;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.like.post.PostLikeService;
import com.example.forum.service.post.PostService;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController implements PostApiDocs {

    private final PostService postService;
    private final PostLikeService postLikeService;
    private final AuthValidator authValidator;

    @GetMapping
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getPosts(
            @RequestParam(defaultValue = "newest") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        SortOrder sortOrder = SortOrder.from(sort);
        List<PostResponseDTO> posts = postService.getPagedPosts(sortOrder, page, size);
        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {


        String username = userDetails == null ? null : authValidator.extractUsername(userDetails);

        PostDetailDTO response = postService.getPostDetail(id, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<PostResponseDTO>> create(
            @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        PostResponseDTO post = postService.createPost(dto, username);
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<CommonResponse<PostResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        PostResponseDTO post = postService.updatePost(id, dto, username);
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);
        postService.deletePost(id, username);

        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Override
    @PostMapping("/{id}/likes")
    public ResponseEntity<CommonResponse<Void>> likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = authValidator.extractUsername(userDetails);

        postLikeService.toggleLike(id, username);
        return ResponseEntity.ok(CommonResponse.success());
    }

    @Override
    @GetMapping("/{id}/likes")
    public ResponseEntity<CommonResponse<Long>> getLikesCount(@PathVariable Long id) {

        long ret = postLikeService.countLikes(id);
        return ResponseEntity.ok(CommonResponse.success(ret));
    }

    @Override
    @GetMapping("/{id}/likes/users")
    public ResponseEntity<CommonResponse<List<LikeUserDTO>>> getLikeUsers(@PathVariable Long id) {

        List<LikeUserDTO> ret = postLikeService.getLikeUsers(id);
        return ResponseEntity.ok(CommonResponse.success(ret));
    }

    @Override
    @PostMapping("/images")
    public ResponseEntity<CommonResponse<String>> uploadPostImage(@RequestParam("file") MultipartFile file) {

        String ret = postService.uploadImage(file);
        return ResponseEntity.ok(CommonResponse.success(ret));
    }
}
