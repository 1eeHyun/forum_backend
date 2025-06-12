package com.example.forum.service.community;

import com.example.forum.dto.community.*;
import com.example.forum.dto.util.OnlineUserDTO;

import java.util.List;

public interface CommunityService {

    CommunityDetailDTO getCommunityDetail(Long id, String username);
    Long create(CommunityRequestDTO dto, String username);
    List<CommunityPreviewDTO> getMyCommunities(String username);
    List<OnlineUserDTO> getOnlineUsers(Long id);

    void addMember(Long communityId, String username);
    void removeMember(Long communityId, String username);

    List<CategoryResponseDTO> getCategories(Long id);
    void addCategory(Long communityId, CategoryRequestDTO dto, String username);
}
