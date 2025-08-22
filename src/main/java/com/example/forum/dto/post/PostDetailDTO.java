package com.example.forum.dto.post;

import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.user.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Getter
@Builder
public class PostDetailDTO {

    private Long id;

    private String title;
    private String content;
    private UserDTO author;

    private CommunityPreviewDTO community;

    private String visibility;

    private List<PostFileDTO> fileUrls;

    private int likeCount;
    private int commentCount;

    private Boolean likedByMe;
    private Boolean isAuthor;

    private List<LikeUserDTO> likeUsers;
    private List<CommentResponseDTO> comments;

    private Instant createdAt;
    private Instant updatedAt;

    private Boolean isHidden;

    @Builder.Default
    private List<String> tags = Collections.emptyList();
}
