package com.example.forum.controller.post.api;

import com.example.forum.controller.post.docs.PostMediaApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostMediaController implements PostMediaApiDocs {

    private final PostService postService;

    @Override
    public ResponseEntity<CommonResponse<String>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        String response = postService.uploadFile(file);
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
