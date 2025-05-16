package com.example.forum.dto.like;

import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class LikeUserDTO {

    private String username;
    private String nickname;
    private ImageDTO imageDTO;
}
