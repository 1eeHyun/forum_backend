package com.example.forum.dto.chat;

import com.example.forum.dto.profile.ProfilePreviewDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // Include all non-null fields in JSON
public class ChatMessageDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("roomId")
    private String roomId;

    @JsonProperty("senderUsername")
    private String senderUsername;

    @JsonProperty("senderProfile")
    private ProfilePreviewDTO senderProfile;

    @JsonProperty("content")
    private String content;

    @JsonProperty("sentAt")
    private LocalDateTime sentAt;
}
