package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;

import java.util.List;

public interface CommunityService {

    CommunityDetailDTO getCommunityDetail(Long id, String username);
    Long create(CommunityRequestDTO dto, String username);
    List<CommunityPreviewDTO> getMyCommunities(String username);

    List<CommunityPreviewDTO> getJoinedCommunities(String target);
}
