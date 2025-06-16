package com.example.forum.service.post.profile;

import com.example.forum.common.SortOrder;
import com.example.forum.dto.post.PostResponseDTO;

import java.util.List;

public interface ProfilePostService {

    List<PostResponseDTO> getProfilePosts(
            String targetUsername,
            String currentUsername,
            SortOrder sort,
            int page,
            int size);





}
