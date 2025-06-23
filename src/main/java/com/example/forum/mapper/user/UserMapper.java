package com.example.forum.mapper.user;

import com.example.forum.dto.user.UserDTO;
import com.example.forum.mapper.image.ImageMapper;
import com.example.forum.model.community.CommunityMember;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;

import java.util.List;

public class UserMapper {

    public static UserDTO toDtoByCommunityMember(CommunityMember member) {

        Profile profile = member.getUser().getProfile();

        return UserDTO.builder()
                .id(member.getId())
                .username(member.getUser().getUsername())
                .nickname(profile != null ? profile.getNickname() : "Unknown")
                .profileImage(profile != null ? ImageMapper.profileToImageDto(profile) : null)
                .role(member.getRole())
                .build();
    }

    public static List<UserDTO> toListDtoByCommunityMemberList(List<CommunityMember> members) {
        return members.stream()
                .map(UserMapper::toDtoByCommunityMember)
                .toList();
    }

    public static UserDTO toDtoWithEmail(User user) {
        return UserDTO.builder()
                .username(user.getUsername())
                .profileImage(ImageMapper.profileToImageDto(user.getProfile()))
                .nickname(user.getProfile().getNickname())
                .email(user.getEmail())
                .build();
    }
}
