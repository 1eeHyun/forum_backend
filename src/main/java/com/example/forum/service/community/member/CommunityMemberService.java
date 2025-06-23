package com.example.forum.service.community.member;

import com.example.forum.dto.user.UserDTO;

import java.util.List;

public interface CommunityMemberService {

    void addMember(Long communityId, String username);
    void leaveCommunity(Long communityId, String username);
    List<UserDTO> getOnlineUsers(Long id);
    List<UserDTO> getNewMembersThisWeek(Long communityId);

    List<UserDTO> getAllMembers(Long communityId, String username);
}
