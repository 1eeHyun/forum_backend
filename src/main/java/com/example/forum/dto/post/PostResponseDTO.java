package com.example.forum.dto.post;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.util.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private UserDTO author;

    private CommunityPreviewDTO community;
    private List<String> imageUrls;
    private int commentCount;
    private int likeCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
