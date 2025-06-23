package com.example.forum.dto.user;

import com.example.forum.dto.image.ImageDTO;
import com.example.forum.model.community.CommunityRole;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

    private Long id;

    private String username;
    private String nickname;
    private ImageDTO profileImage;

    // --------- Optional ---------
    private CommunityRole role;
    private String email;
}
