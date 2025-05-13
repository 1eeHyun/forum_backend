package com.example.forum.controller.post;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostRequestDTO;
import com.example.forum.dto.post.PostResponseDTO;
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

    @Override
    @GetMapping("/asc")
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllAsc() {

        List<PostResponseDTO> posts = postService.getAllPostsByASC();
        return ResponseEntity.ok(CommonResponse.success(posts));
    }

    @Override
    @GetMapping("/desc")
    public ResponseEntity<CommonResponse<List<PostResponseDTO>>> getAllDesc() {

        List<PostResponseDTO> posts = postService.getAllPostsByDESC();
        return ResponseEntity.ok(CommonResponse.success(posts));
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
}
