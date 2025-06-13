package com.example.forum.service.community;

import com.example.forum.dto.community.*;
import com.example.forum.dto.util.UserDTO;

import java.util.List;

public interface CommunityService {

    CommunityDetailDTO getCommunityDetail(Long id, String username);
    Long create(CommunityRequestDTO dto, String username);
    List<CommunityPreviewDTO> getMyCommunities(String username);
    List<UserDTO> getOnlineUsers(Long id);
    List<UserDTO> getNewMembersThisWeek(Long communityId);

    void addMember(Long communityId, String username);
    void removeMember(Long communityId, String username);

    List<CategoryResponseDTO> getCategories(Long communityId);
    void addCategory(Long communityId, CategoryRequestDTO dto, String username);

    void addRule(Long communityId, CommunityRuleRequestDTO request, String username);
}
