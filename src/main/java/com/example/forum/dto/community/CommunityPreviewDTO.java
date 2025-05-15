package com.example.forum.dto.community;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityPreviewDTO {

    private Long id;
    private String name;
    private String imageUrl;
}
