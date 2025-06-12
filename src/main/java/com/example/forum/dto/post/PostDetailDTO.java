package com.example.forum.dto.post;

import com.example.forum.dto.comment.CommentResponseDTO;
import com.example.forum.dto.community.CommunityPreviewDTO;
import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.util.UserDTO;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
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

    private List<String> imageUrls;

    private int likeCount;
    private int commentCount;

    private Boolean likedByMe;
    private Boolean isAuthor;

    private List<LikeUserDTO> likeUsers;
    private List<CommentResponseDTO> comments;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
