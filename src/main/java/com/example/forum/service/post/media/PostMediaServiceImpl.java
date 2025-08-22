package com.example.forum.service.post.media;

import com.example.forum.service.common.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostMediaServiceImpl implements PostMediaService {

    private final S3Service s3Service;

    private static final Set<String> ALLOWED = Set.of(
            "image/jpeg", "image/png", "image/webp",
            "video/mp4", "video/quicktime"
    );

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Empty file");
        }
        String ct = file.getContentType();
        if (ct == null || !ALLOWED.contains(ct)) {
            throw new IllegalArgumentException("Only JPG/PNG/WEBP or common videos are supported.");
        }

        return s3Service.upload(file);
    }
}
