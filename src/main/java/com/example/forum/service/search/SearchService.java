package com.example.forum.service.search;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.search.SearchResponseDTO;

import java.util.List;

public interface SearchService {

    SearchResponseDTO searchAll(String keyword, String username);
    List<ProfilePreviewDTO> searchUsers(String keyword);
    List<PostPreviewDTO> searchPosts(String keyword, String username);
    List<CommunityPreviewDTO> searchCommunities(String keyword, String username);
}
