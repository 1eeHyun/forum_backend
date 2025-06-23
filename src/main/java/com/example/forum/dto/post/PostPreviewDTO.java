package com.example.forum.dto.post;

import com.example.forum.dto.image.ImageDTO;
import com.example.forum.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PostPreviewDTO {

    private Long id;
    private String title;
    private String content;
    private List<String> imageUrls;

    private int likeCount;
    private int commentCount;
    private String createdAtFormatted;

    private String communityName;
    private Long communityId;
    private ImageDTO communityProfilePicture;

    private String authorNickname;
    private UserDTO author;
}
