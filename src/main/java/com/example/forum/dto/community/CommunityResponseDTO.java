package com.example.forum.dto.community;

import com.example.forum.dto.post.AuthorDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommunityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime createdAt;
    private AuthorDTO author;
}
