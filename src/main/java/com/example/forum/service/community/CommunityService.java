package com.example.forum.service.community;

import com.example.forum.dto.community.CategoryRequestDTO;
import com.example.forum.dto.community.CommunityDetailDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.community.CommunityRequestDTO;
import com.example.forum.dto.util.OnlineUserDTO;

import java.util.List;

public interface CommunityService {

    CommunityDetailDTO getCommunityDetail(Long id, String username);
    Long create(CommunityRequestDTO dto, String username);
    List<CommunityPreviewDTO> getMyCommunities(String username);
    List<OnlineUserDTO> getOnlineUsers(Long id);

    void addMember(String username);
    void removeMember(String username);
    void addCategory(Long communityId, CategoryRequestDTO dto, String username);
}
