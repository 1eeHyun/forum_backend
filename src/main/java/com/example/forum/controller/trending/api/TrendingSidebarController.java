package com.example.forum.controller.trending.api;

import com.example.forum.controller.trending.docs.TrendingSidebarApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.trend.TrendingSidebarDTO;
import com.example.forum.service.trending.TrendingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/trending")
@RequiredArgsConstructor
public class TrendingSidebarController implements TrendingSidebarApiDocs {

    private final TrendingService trendingService;

    @Override
    public ResponseEntity<CommonResponse<TrendingSidebarDTO>> getTrendingSidebar() {

        TrendingSidebarDTO response = trendingService.getTrendingSidebarData();
        return ResponseEntity.ok(CommonResponse.success(response));
    }
}
