package com.example.forum.service.post.trending;

import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;

import java.util.List;

public interface TrendingPostService {

    List<PostResponseDTO> getTrendingPosts(String username);
    List<PostPreviewDTO> getTopPostsThisWeek(String username);
}
