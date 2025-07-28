package com.example.forum.service.post.view;

import com.example.forum.dto.post.PostPreviewDTO;

import java.util.List;

public interface ViewedPostService {

    List<PostPreviewDTO> getRecentlyViewedPosts(String username);
    List<PostPreviewDTO> getPreviewPostsByIds(List<Long> ids, String username);
}
