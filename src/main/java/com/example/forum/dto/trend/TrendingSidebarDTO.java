package com.example.forum.dto.trend;

import com.example.forum.dto.community.CommunityPreviewDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class TrendingSidebarDTO {

    private List<CommunityPreviewDTO> trendingCommunities;
    private List<String> hotTags;

    // can add more
}
