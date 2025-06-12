package com.example.forum.mapper.image;

import com.example.forum.dto.image.ImageDTO;
import com.example.forum.model.profile.Profile;
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

    public static ImageDTO profileToImageDto(Profile profile) {

        return ImageDTO.builder()
                .imageUrl(profile.getImageUrl())
                .imagePositionX(profile.getImagePositionX())
                .imagePositionY(profile.getImagePositionY())
                .build();
    }
}
