package com.example.forum.dto.post;

import com.example.forum.model.post.FileType;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PostFileDTO {
    private String fileUrl;
    private FileType type; // enum: IMAGE, VIDEO
}
