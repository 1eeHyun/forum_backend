package com.example.forum.dto.follow;

import com.example.forum.dto.util.ImageDTO;
import com.example.forum.mapper.util.ImageMapper;
import com.example.forum.model.user.User;
import lombok.*;

@Getter
@Setter
public class FollowUserDTO {

    private String username;
    private String nickname;
    private ImageDTO imageDto;

    public FollowUserDTO(User user) {
        this.username = user.getUsername();
        this.nickname = user.getProfile().getNickname();
        this.imageDto = ImageMapper.profileToImageDto(user.getProfile());
    }
}
