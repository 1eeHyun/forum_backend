package com.example.forum.controller.trending.docs;

import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.trend.TrendingSidebarDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

@Tag(name = "Trending sidebar related API", description = "Trending sidebar")
public interface TrendingSidebarApiDocs {

    @Operation(
            summary = "Get trending sidebar data",
            description = "Returns trending communities and hot tags for the sidebar.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sidebar data fetched successfully")
            }
    )
    @GetMapping("/sidebar")
    ResponseEntity<CommonResponse<TrendingSidebarDTO>> getTrendingSidebar();
}
