package com.example.forum.controller.search.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.search.SearchResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
            @RequestParam("query") String query
    );
}
