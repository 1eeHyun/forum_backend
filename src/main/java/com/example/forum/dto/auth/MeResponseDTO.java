package com.example.forum.dto.auth;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MeResponseDTO {

    private String username;
    private String email;
    private String nickname;
    private String imageUrl;
}

