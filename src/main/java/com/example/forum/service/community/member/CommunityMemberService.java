package com.example.forum.service.community.member;

import com.example.forum.dto.util.UserDTO;

import java.util.List;

public interface CommunityMemberService {

    void addMember(Long communityId, String username);
    void removeMember(Long communityId, String username);
    List<UserDTO> getOnlineUsers(Long id);
    List<UserDTO> getNewMembersThisWeek(Long communityId);

    List<UserDTO> getAllMembers(Long communityId);
}
