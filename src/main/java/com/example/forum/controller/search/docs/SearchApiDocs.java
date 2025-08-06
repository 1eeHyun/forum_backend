package com.example.forum.controller.search.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.search.SearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Search", description = "Search related API on the navigation bar")
public interface SearchApiDocs {

    @Operation(
            summary = "Search posts and communities",
            description = "Searches for posts and communities that match the given keyword. Returns a combined result.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search results returned successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SearchResponseDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid query parameter",
                            content = @Content
                    )
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<SearchResponseDTO>> search(
            @Parameter(description = "Search keyword", required = true)
            @RequestParam("query") String query,

            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Search users by nickname",
            description = "Returns a list of users whose nickname matches the keyword.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "List of users",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ProfilePreviewDTO.class)
                    )
            )
    )
    @GetMapping("/users")
    ResponseEntity<CommonResponse<List<ProfilePreviewDTO>>> searchUsers(
            @Parameter(description = "User username", required = true)
            @RequestParam("keyword") String keyword
    );

    @Operation(
            summary = "Search posts by title or content",
            description = "Returns a list of posts whose title or content matches the keyword.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "List of posts",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PostPreviewDTO.class)
                    )
            )
    )
    @GetMapping("/posts")
    ResponseEntity<CommonResponse<List<PostPreviewDTO>>> searchPosts(
            @Parameter(description = "Post keyword", required = true)
            @RequestParam("keyword") String keyword,

            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );

    @Operation(
            summary = "Search communities by name or description",
            description = "Returns a list of communities whose name or description matches the keyword.",
            responses = @ApiResponse(
                    responseCode = "200",
                    description = "List of communities",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CommunityPreviewDTO.class)
                    )
            )
    )
    @GetMapping("/communities")
    ResponseEntity<CommonResponse<List<CommunityPreviewDTO>>> searchCommunities(
            @Parameter(description = "Community keyword", required = true)
            @RequestParam("keyword") String keyword,

            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails
    );
}
