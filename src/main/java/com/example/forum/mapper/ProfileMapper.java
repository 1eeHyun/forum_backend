package com.example.forum.mapper;

import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;

import java.util.List;

public class ProfileMapper {

    public static ProfileResponseDTO toDTO(User targetUser, boolean isMe, List<Post> posts) {
        return ProfileResponseDTO.builder()
                .username(targetUser.getUsername())
                .nickname(targetUser.getProfile().getNickname())
                .bio(targetUser.getProfile().getBio())
                .imageUrl(targetUser.getProfile().getImageUrl())
                .isMe(isMe)
                .posts(posts.stream().map(PostMapper::toDTO).toList())
                .build();
    }
}
