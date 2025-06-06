package com.example.forum.service.search;

import com.example.forum.dto.search.SearchResponseDTO;

public interface SearchService {

    SearchResponseDTO searchAll(String keyword);
}
