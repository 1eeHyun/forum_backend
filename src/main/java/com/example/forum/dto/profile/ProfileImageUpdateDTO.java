package com.example.forum.dto.profile;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProfileImageUpdateDTO {
    private MultipartFile image;
    private Double positionX;
    private Double positionY;
}
