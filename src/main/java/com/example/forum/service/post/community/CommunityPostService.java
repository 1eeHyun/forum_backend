package com.example.forum.service.post.community;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;

import java.util.List;
import java.util.Map;

public interface CommunityPostService {

    List<PostPreviewDTO> getRecentPostsFromJoinedCommunities(String username);
    List<PostResponseDTO> getCommunityPosts(Long communityId, SortOrder sort, int page, int size, String category);
    List<PostResponseDTO> getTopPostsThisWeek(Long communityId, int size);
    Map<String, List<PostResponseDTO>> getTopPostsThisWeekByCategories(Long communityId, int size);
}
