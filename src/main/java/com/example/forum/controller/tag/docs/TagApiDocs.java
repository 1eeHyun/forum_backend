package com.example.forum.controller.tag.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.post.PostResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Tag", description = "Tag related API")
public interface TagApiDocs {

    @Operation(
            summary = "Suggest tags",
            description = "Return tag suggestions based on a search query.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of suggested tags returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = String.class))
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "Invalid query parameter", content = @Content)
            }
    )
    @GetMapping("/suggest")
    ResponseEntity<CommonResponse<List<String>>> suggest(
            @Parameter(description = "Search query", required = true)
            @RequestParam("q") @NotBlank String q,
            @Parameter(description = "Maximum number of results", example = "8")
            @RequestParam(value = "limit", defaultValue = "8") @Min(1) @Max(20) int limit
    );

    @Operation(
            summary = "Top tags",
            description = "Return most frequently used tags.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "List of top tags returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = String.class))
                            )
                    )
            }
    )
    @GetMapping("/top")
    ResponseEntity<CommonResponse<List<String>>> topTags(
            @Parameter(description = "Number of tags to return", example = "10")
            @RequestParam(value = "limit", defaultValue = "10") @Min(1) @Max(50) int limit
    );

    @Operation(
            summary = "Get posts by tag",
            description = "Return a paginated list of posts associated with a given tag.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Posts for the tag returned successfully",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Page.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Tag not found", content = @Content)
            }
    )
    @GetMapping("/{tag}/posts")
    ResponseEntity<CommonResponse<Page<PostResponseDTO>>> getPostsByTag(
            @Parameter(description = "Tag name", required = true)
            @PathVariable("tag") @NotBlank String tag,
            @Parameter(hidden = true) Pageable pageable
    );

    @Operation(
            summary = "Replace all tags for a post",
            description = "Replace existing tags with new ones. Works only if the post is PUBLIC.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tags replaced successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid tag list", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Not authorized to edit tags", content = @Content)
            }
    )
    @PatchMapping("/posts/{postId}/tags")
    ResponseEntity<CommonResponse<Void>> replaceTags(
            @PathVariable Long postId,
            @RequestBody @Valid TagsRequest body);

    @Operation(
            summary = "Add tags to a post",
            description = "Add tags to a post without removing existing ones. Works only if the post is PUBLIC and total tags ≤ 5.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tags added successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid tag list", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Not authorized to edit tags", content = @Content)
            }
    )
    @PostMapping("/posts/{postId}/tags")
    ResponseEntity<CommonResponse<Void>> addTags(
            @PathVariable Long postId,
            @RequestBody @Valid TagsRequest body);

    @Operation(
            summary = "Remove tags from a post",
            description = "Remove specific tags from a post.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tags removed successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid tag list", content = @Content),
                    @ApiResponse(responseCode = "403", description = "Not authorized to edit tags", content = @Content)
            }
    )
    @DeleteMapping("/posts/{postId}/tags")
    ResponseEntity<CommonResponse<Void>> removeTags(
            @PathVariable Long postId,
            @RequestBody @Valid TagsRequest body);

    @Schema(description = "Tag list payload")
    class TagsRequest {
        @Schema(description = "Tags to apply", example = "[\"spring-boot\",\"java\"]")
        public List<String> tags;
        public List<String> getTags() { return tags; }
        public void setTags(List<String> tags) { this.tags = tags; }
    }
}
