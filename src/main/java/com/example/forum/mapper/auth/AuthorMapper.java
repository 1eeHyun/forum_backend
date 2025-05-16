package com.example.forum.mapper.auth;

import com.example.forum.dto.auth.MeResponseDTO;
import com.example.forum.dto.post.AuthorDTO;
import com.example.forum.dto.util.ImageDTO;
import com.example.forum.model.user.User;
import lombok.Getter;

@Getter
public class AuthorMapper {

    public static AuthorDTO toDto(User user) {
        return AuthorDTO.builder()
                .username(user.getUsername())
                .nickname(user.getProfile().getNickname())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(user.getProfile().getImageUrl())
                        .imagePositionX(user.getProfile().getImagePositionX())
                        .imagePositionY(user.getProfile().getImagePositionY())
                        .build())
                .build();
    }

    public static MeResponseDTO toMeDto(User user) {
        return MeResponseDTO.builder()
                .username(user.getUsername())
                .imageDTO(ImageDTO.builder()
                        .imageUrl(user.getProfile().getImageUrl())
                        .imagePositionX(user.getProfile().getImagePositionX())
                        .imagePositionY(user.getProfile().getImagePositionY())
                        .build())
                .nickname(user.getProfile().getNickname())
                .email(user.getEmail())
                .build();
    }
}
