package com.example.forum.dto.auth;


import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeResponseDTO {

    private String username;
    private String email;
    private String nickname;
    private ImageDTO imageDTO;
}

