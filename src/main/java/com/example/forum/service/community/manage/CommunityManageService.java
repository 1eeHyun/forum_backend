package com.example.forum.service.community;

import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CategoryResponseDTO;
import com.example.forum.dto.community.CommunityRuleRequestDTO;
import com.example.forum.dto.community.CommunityRuleResponseDTO;

import java.util.List;

public interface CommunityManageService {

    // Category
    List<CategoryResponseDTO> getCategories(Long communityId);
    void addCategory(Long communityId, CategoryRequestDTO dto, String username);

    // Rule
    List<CommunityRuleResponseDTO> getRules(Long communityId);
    void addRule(Long communityId, CommunityRuleRequestDTO request, String username);
    void updateRule(Long communityId, Long ruleId, CommunityRuleRequestDTO request, String username);
    void deleteRule(Long communityId, Long ruleId, String username);
}
