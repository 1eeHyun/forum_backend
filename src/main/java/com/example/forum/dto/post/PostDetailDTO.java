package com.example.forum.dto.post;

import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PostDetailDTO {

    private Long id;
    private String title;
    private String content;
    private AuthorDTO author;
    private ImageDTO imageDTO;
}
