package com.example.forum.dto.profile;

import com.example.forum.dto.post.PostResponseDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProfileResponseDTO {

    private String username;
    private String nickname;
    private String bio;
    private String imageUrl;
    private Boolean isMe;
    private List<PostResponseDTO> posts;
}
