package com.example.forum.helper.chat;

import com.example.forum.dto.chat.ChatMessageDTO;
import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.example.forum.dto.image.ImageDTO;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.validator.auth.AuthValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ChatMessageBuilder {

    private final AuthValidator userValidator;

    public ChatMessageDTO enrichSenderProfile(ChatMessageDTO dto) {
        User sender = userValidator.validateUserByUsername(dto.getSenderUsername());
        Profile profile = sender.getProfile();

        dto.setSenderProfile(ProfilePreviewDTO.builder()
                .username(sender.getUsername())
                .nickname(profile.getNickname())
                .imageDto(ImageDTO.builder()
                        .imageUrl(profile.getImageUrl())
                        .imagePositionX(profile.getImagePositionX())
                        .imagePositionY(profile.getImagePositionY())
                        .build())
                .build());

        return dto;
    }
}
