package com.example.forum.dto.community;

import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityPreviewDTO {

    private Long id;
    private String name;
    private ImageDTO imageDTO;
}
