package com.example.forum.dto.profile;

import com.example.forum.dto.follow.FollowUserDTO;
import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileResponseDTO {

    private String username;
    private String nickname;
    private String bio;
    private ImageDTO imageDTO;
    private Boolean isMe;

    private List<FollowUserDTO> followers;
    private List<FollowUserDTO> followings;
}
