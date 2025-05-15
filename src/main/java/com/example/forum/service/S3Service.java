package com.example.forum.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.forum.exception.s3.UploadFailedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    public String upload(MultipartFile file) {

        String filename = UUID.randomUUID() + "-" + file.getOriginalFilename();

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            amazonS3.putObject(new PutObjectRequest(bucket, filename, file.getInputStream(), metadata));

            return amazonS3.getUrl(bucket, filename).toString();
        } catch (IOException e) {
            throw new UploadFailedException();
        }
    }

    public void delete(String url) {
        String key = extractKeyFromUrl(url);
        amazonS3.deleteObject(bucket, key);
    }

    private String extractKeyFromUrl(String url) {
        return url.substring(url.indexOf(".com/") + 5);
    }

}
