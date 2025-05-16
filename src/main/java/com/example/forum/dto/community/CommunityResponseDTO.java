package com.example.forum.dto.community;

import com.example.forum.dto.util.AuthorDTO;
import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Builder
public class CommunityResponseDTO {

    private Long id;
    private String name;
    private String description;
    private ImageDTO imageDTO;
    private LocalDateTime createdAt;

    private AuthorDTO author;

    private Set<AuthorDTO> members;
}
