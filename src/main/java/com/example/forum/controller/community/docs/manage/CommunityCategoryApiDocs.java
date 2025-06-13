package com.example.forum.controller.community.docs.manage;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CategoryResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Tag(name = "Community Category", description = "Community Category related API")
public interface CommunityCategoryApiDocs {

    @Operation(
            summary = "Get categories of a community",
            description = "Retrieves all categories associated with the specified community.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved categories",
                            content = @Content(array = @ArraySchema(schema = @Schema(implementation = CategoryResponseDTO.class)))),
                    @ApiResponse(responseCode = "404", description = "Community not found")
            }
    )
    @GetMapping
    ResponseEntity<CommonResponse<List<CategoryResponseDTO>>> getCategories(
            @Parameter(description = "ID of the community to retrieve categories from", required = true)
            @PathVariable Long communityId
    );

    @Operation(
            summary = "Add a new category to a community",
            description = "Adds a new category to the specified community. Only accessible by community managers.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Category successfully added"),
                    @ApiResponse(responseCode = "404", description = "Community not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input data"),
                    @ApiResponse(responseCode = "403", description = "User is not authorized to manage this community")
            }
    )
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Category information including name and optional description",
            required = true,
            content = @Content(schema = @Schema(implementation = CategoryRequestDTO.class))
    )
    @PostMapping
    ResponseEntity<CommonResponse<Void>> addCategory(
            @Parameter(description = "ID of the community to which the category will be added", required = true)
            @PathVariable Long communityId,

            @Valid @RequestBody CategoryRequestDTO dto,

            @Parameter(hidden = true)
            @AuthenticationPrincipal UserDetails userDetails
    );
}
