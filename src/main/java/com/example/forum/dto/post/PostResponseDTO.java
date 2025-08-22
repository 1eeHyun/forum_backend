package com.example.forum.dto.post;

import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private UserDTO author;

    private CommunityPreviewDTO community;
    private List<PostFileDTO> fileUrls;

    private int commentCount;
    private int likeCount;
    private Instant createdAt;
    private Instant updatedAt;

    private Boolean isHidden;

    @Builder.Default
    private List<String> tags = Collections.emptyList();
}
