package com.example.forum.dto.community;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private String creatorNickname;
}
