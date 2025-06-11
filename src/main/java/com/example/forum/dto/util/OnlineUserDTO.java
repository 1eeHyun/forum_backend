package com.example.forum.dto.util;

import com.example.forum.model.community.CommunityRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class OnlineUserDTO {

    private Long id;

    private String username;
    private String nickname;
    private ImageDTO imageDto;
    private CommunityRole role;
}
