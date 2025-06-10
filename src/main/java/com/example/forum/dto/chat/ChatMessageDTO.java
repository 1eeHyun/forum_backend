package com.example.forum.dto.chat;

import com.example.forum.dto.profile.ProfilePreviewDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private Long id;
    private String roomId;

    private String senderUsername;
    private ProfilePreviewDTO senderProfile;
    private String content;

    private LocalDateTime sentAt;
}
