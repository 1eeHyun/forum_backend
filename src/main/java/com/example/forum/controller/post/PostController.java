package com.example.forum.controller.post;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.service.like.post.PostLikeService;
import com.example.forum.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController implements PostApiDocs {

    private final PostService postService;
    private final PostLikeService postLikeService;

    @Override
    @GetMapping("/accessible/asc")
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllPublicPostAsc(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<PostResponseDTO> posts;
        if (userDetails == null)
            posts = postService.getAccessiblePostsByASC(null);
        else
            posts = postService.getAccessiblePostsByASC(userDetails.getUsername());

//        List<PostResponseDTO> posts = postService.getAccessiblePostsByASC(userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    @GetMapping("/accessible/desc")
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllPublicPostDesc(
            @AuthenticationPrincipal UserDetails userDetails) {

        List<PostResponseDTO> posts;
        if (userDetails == null)
            posts = postService.getAccessiblePostsByDESC(null);
        else
            posts = postService.getAccessiblePostsByDESC(userDetails.getUsername());

//        List<PostResponseDTO> posts = postService.getAccessiblePostsByDESC(userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<CommonResponse<PostDetailDTO>> getPostDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        String username = userDetails != null ? userDetails.getUsername() : null;

        PostDetailDTO response = postService.getPostDetail(id, username);
        return ResponseEntity.ok(CommonResponse.success(response));
    }

    @Override
    @PostMapping
    public ResponseEntity<CommonResponse<PostResponseDTO>> create(
            @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        PostResponseDTO post = postService.createPost(dto, userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    @PostMapping("/{id}")
    public ResponseEntity<CommonResponse<PostResponseDTO>> update(
            @PathVariable Long id,
            @RequestBody PostRequestDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        PostResponseDTO post = postService.updatePost(id, dto, userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success(post));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonResponse<Void>> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        postService.deletePost(id, userDetails.getUsername());
        return ResponseEntity.ok(CommonResponse.success(null));
    }

    @Override
    @PostMapping("/{id}/likes")
    public ResponseEntity<CommonResponse<Void>> likePost(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {

        postLikeService.toggleLike(id, userDetails.getUsername());
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
}
