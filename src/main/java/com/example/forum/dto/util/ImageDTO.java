package com.example.forum.dto.util;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ImageDTO {

    private String imageUrl;
    private Double imagePositionX;
    private Double imagePositionY;
}
