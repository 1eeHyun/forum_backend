package com.example.forum.service.bookmark;

import com.example.forum.dto.post.PostPreviewDTO;

import java.util.List;

public interface BookmarkService {

    void toggleBookmark(Long postId, String username);
    boolean isBookmarked(Long postId, String username);
    List<PostPreviewDTO> getBookmarkedPosts(String username);
}
