package com.example.forum.mapper.profile;

import com.example.forum.dto.follow.FollowUserDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;

import java.util.List;

public class ProfileMapper {

    public static ProfileResponseDTO toDTO(User targetUser, boolean isMe, int postCount, List<FollowUserDTO> followers, List<FollowUserDTO> followings) {
        return ProfileResponseDTO.builder()
                .username(targetUser.getUsername())
                .nickname(targetUser.getProfile().getNickname())
                .bio(targetUser.getProfile().getBio())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(targetUser.getProfile().getImageUrl())
                        .imagePositionX(targetUser.getProfile().getImagePositionX())
                        .imagePositionY(targetUser.getProfile().getImagePositionY())
                        .build())
                .isMe(isMe)
                .totalPostCount(postCount)
                .followers(followers)
                .followings(followings)
                .build();
    }

    public static ProfilePreviewDTO toProfilePreviewDTO(User user) {
        Profile profile = user.getProfile();
        return ProfilePreviewDTO.builder()
                .username(user.getUsername())
                .nickname(profile.getNickname())
                .imageDto(ImageDTO.builder()
                        .imageUrl(profile.getImageUrl())
                        .imagePositionX(profile.getImagePositionX())
                        .imagePositionY(profile.getImagePositionY())
                        .build())
                .build();
    }

}
