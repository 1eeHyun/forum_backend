package com.example.forum.service.post.media;

import org.springframework.web.multipart.MultipartFile;

public interface PostMediaService {

    String uploadFile(MultipartFile file);
}
