package com.example.forum.dto.post;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorDTO {
    private String username;
    private String nickname;
    private String imageUrl;
}
