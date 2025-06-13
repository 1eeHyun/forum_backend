package com.example.forum.service.community;

import com.example.forum.dto.community.*;
import com.example.forum.dto.util.UserDTO;

import java.util.List;

public interface CommunityService {

    CommunityDetailDTO getCommunityDetail(Long id, String username);
    Long create(CommunityRequestDTO dto, String username);
    List<CommunityPreviewDTO> getMyCommunities(String username);

    // Member
    void addMember(Long communityId, String username);
    void removeMember(Long communityId, String username);
    List<UserDTO> getOnlineUsers(Long id);
    List<UserDTO> getNewMembersThisWeek(Long communityId);

    // Category
    List<CategoryResponseDTO> getCategories(Long communityId);
    void addCategory(Long communityId, CategoryRequestDTO dto, String username);

    // Rule
    List<CommunityRuleResponseDTO> getRules(Long communityId);
    void addRule(Long communityId, CommunityRuleRequestDTO request, String username);
    void updateRule(Long communityId, Long ruleId, CommunityRuleRequestDTO request, String username);
    void deleteRule(Long communityId, Long ruleId, String username);
}
