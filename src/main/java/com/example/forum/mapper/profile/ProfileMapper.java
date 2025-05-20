package com.example.forum.mapper.profile;

import com.example.forum.dto.follow.FollowUserDTO;
import com.example.forum.dto.profile.ProfileResponseDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.mapper.post.PostMapper;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;

import java.util.List;

public class ProfileMapper {

    public static ProfileResponseDTO toDTO(User targetUser, boolean isMe, List<Post> posts, List<FollowUserDTO> followers, List<FollowUserDTO> followings) {
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
                .posts(posts.stream().map(PostMapper::toPostResponseDTO).toList())
                .followers(followers)
                .followings(followings)
                .build();
    }
}
