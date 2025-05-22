package com.example.forum.dto.post;

import com.example.forum.dto.util.AuthorDTO;
import com.example.forum.dto.util.ImageDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostPreviewDTO {

    private Long id;
    private String title;
    private List<String> thumbnailUrls;
    private int likeCount;
    private int commentCount;
    private String createdAtFormatted;

    private String communityName;
    private Long communityId;
    private ImageDTO communityProfilePicture;

    private String authorNickname;
    private AuthorDTO author;
}
