package com.example.forum.dto.chat;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDTO {

    private String roomId;
    private Long senderId;
    private String content;
    private LocalDateTime sentAt;
}
