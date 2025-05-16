package com.example.forum.mapper;

import com.example.forum.dto.util.ImageDTO;
import lombok.Getter;

@Getter
public class ImageMapper {

    public static ImageDTO toDto(String imageUrl, Double x, Double y) {

        return ImageDTO.builder()
                .imageUrl(imageUrl)
                .imagePositionX(x)
                .imagePositionY(y)
                .build();
    }
}
