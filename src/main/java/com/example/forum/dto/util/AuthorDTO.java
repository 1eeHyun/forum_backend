package com.example.forum.dto.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthorDTO {
    private String username;
    private String nickname;
    private ImageDTO imageDTO;
}
