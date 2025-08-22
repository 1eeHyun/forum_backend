package com.example.forum.dto.chat;

import com.example.forum.dto.profile.ProfilePreviewDTO;
import lombok.*;

import java.time.Instant;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomDTO {

    private String roomId;
    private ProfilePreviewDTO user;
    private String lastMessage;
    private Instant lastMessageAt;
}
