package com.example.forum.service.post.media;

import com.example.forum.service.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class PostMediaServiceImpl implements PostMediaService {

    private final S3Service s3Service;

    @Override
    public String uploadFile(MultipartFile file) {
        return s3Service.upload(file);
    }
}
