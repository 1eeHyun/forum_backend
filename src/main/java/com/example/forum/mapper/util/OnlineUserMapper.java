package com.example.forum.mapper.util;

import com.example.forum.dto.util.OnlineUserDTO;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.profile.Profile;

import java.util.List;

public class OnlineUserMapper {

    public static List<OnlineUserDTO> toDTO(List<CommunityMember> members) {
        return members.stream()
                .map(OnlineUserMapper::toDTO)
                .toList();
    }

    public static OnlineUserDTO toDTO(CommunityMember member) {

        Profile profile = member.getUser().getProfile();

        return OnlineUserDTO.builder()
                .id(member.getId())
                .username(member.getUser().getUsername())
                .nickname(profile != null ? profile.getNickname() : "Unknown")
                .imageDto(profile != null ? ImageMapper.profileToImageDto(profile) : null)
                .role(member.getRole())
                .build();
    }
}
