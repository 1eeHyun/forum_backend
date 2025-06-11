package com.example.forum.dto.search;


import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
@Builder
public class SearchResponseDTO {

    private List<PostPreviewDTO> posts;
    private List<CommunityPreviewDTO> communities;
    private List<ProfilePreviewDTO> users;
}
