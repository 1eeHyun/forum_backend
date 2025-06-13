package com.example.forum.dto.image;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ImageUploadRequestDTO {

    private MultipartFile image;
    private Double positionX;
    private Double positionY;
}
