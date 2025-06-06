package com.example.forum.controller.search.api;

import com.example.forum.controller.search.docs.SearchApiDocs;
import com.example.forum.dto.CommonResponse;
import com.example.forum.dto.search.SearchResponseDTO;
import com.example.forum.service.search.SearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController implements SearchApiDocs {

    private final SearchService searchService;

    @Override
    public ResponseEntity<CommonResponse<SearchResponseDTO>> search(String query) {

        SearchResponseDTO responses = searchService.searchAll(query);
        return ResponseEntity.ok(CommonResponse.success(responses));
    }
}
