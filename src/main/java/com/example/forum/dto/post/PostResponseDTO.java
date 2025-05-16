package com.example.forum.dto.post;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.util.AuthorDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private AuthorDTO author;
    private CommunityPreviewDTO community;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
