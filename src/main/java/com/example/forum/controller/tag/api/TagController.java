package com.example.forum.controller.tag.api;

import com.example.forum.controller.tag.docs.TagApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.post.Post;
import com.example.forum.service.tag.TaggingService;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Slf4j
public class TagController implements TagApiDocs {

    private final TaggingService taggingService;

    @Override
    public ResponseEntity<CommonResponse<List<String>>> suggest(
            @Parameter(description = "Search query", required = true) @NotBlank String q,
            @Parameter(description = "Maximum number of results", example = "8") @Min(1) @Max(20) int limit) {


        List<String> result = taggingService.suggest(q, limit);
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    @Override
    public ResponseEntity<CommonResponse<List<String>>> topTags(int limit) {

        List<String> result = taggingService.topTags(Math.max(1, Math.min(50, limit)));
        return ResponseEntity.ok(CommonResponse.success(result));
    }

    @Override
    public ResponseEntity<CommonResponse<Page<PostResponseDTO>>> getPostsByTag(
            String tag,
            Pageable pageable) {

        Page<Post> page = taggingService.getPostsByTag(tag, pageable);

        Page<PostResponseDTO> dto = page.map(p -> PostMapper.toPostResponseDTO(p, false, false));
        return ResponseEntity.ok(CommonResponse.success(dto));
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> replaceTags(
            Long postId,
            TagsRequest body) {

        taggingService.setTagsForPost(postId, body == null ? List.of() : body.getTags());
        // 204 No Content
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> addTags(
            Long postId,
            TagsRequest body) {

        taggingService.addTagsToPost(postId, body == null ? List.of() : body.getTags());
        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<CommonResponse<Void>> removeTags(
            Long postId,
            TagsRequest body) {

        taggingService.removeTagsFromPost(postId, body == null ? List.of() : body.getTags());
        return ResponseEntity.noContent().build();
    }
}
