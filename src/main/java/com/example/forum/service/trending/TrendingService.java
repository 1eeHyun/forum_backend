package com.example.forum.service.trending;

import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.dto.trend.TrendingSidebarDTO;

import java.util.List;

public interface TrendingService {

    List<PostResponseDTO> getTrendingPosts(String username);
    TrendingSidebarDTO getTrendingSidebarData(String username);
}
