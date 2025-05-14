package com.example.forum.mapper;

import com.example.forum.dto.auth.MeResponseDTO;
import com.example.forum.dto.post.AuthorDTO;
import com.example.forum.model.user.User;
import lombok.Getter;

@Getter
public class AuthorMapper {

    public static AuthorDTO toDto(User user) {
        return AuthorDTO.builder()
                .username(user.getUsername())
                .nickname(user.getProfile().getNickname())
                .imageUrl(user.getProfile().getImageUrl())
                .build();
    }

    public static MeResponseDTO toMeDto(User user) {
        return MeResponseDTO.builder()
                .username(user.getUsername())
                .imageUrl(user.getProfile().getImageUrl())
                .nickname(user.getProfile().getNickname())
                .email(user.getEmail())
                .build();
    }
}
