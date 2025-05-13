package com.example.forum.service.community;

import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.community.CommunityResponseDTO;

import java.util.List;

public interface CommunityService {

    CommunityResponseDTO create(CommunityRequestDTO dto, String username);
    List<CommunityResponseDTO> getMyCommunities(String username);
}
