package com.example.forum.mapper.post;

import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.AuthorDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.auth.AuthorMapper;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.util.ImageMapper;
import com.example.forum.model.post.Post;
import com.example.forum.model.user.User;

public class PostMapper {
    public static PostResponseDTO toPostResponseDTO(Post post) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(AuthorMapper.toDto(post.getAuthor()))
                .community(CommunityMapper.toPreviewDTO(post.getCommunity()))
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

//    private Long id;
//    private String title;
//    private String content;
//    private AuthorDTO author;
//    private CommunityPreviewDTO community;
//    private String visibility;
//    private ImageDTO contentImageDTO;
//    private int likeCount;
//    private int commentCount;
//    private List<LikeUserDTO> likeUsers;
//    private List<CommentResponseDTO> comments;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;

    public static PostDetailDTO toPostDetailDTO(Post post, User viewer) {
        return PostDetailDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                // Author of the post
                .author(AuthorDTO.builder()
                        .username(post.getAuthor().getUsername())
                        .nickname(post.getAuthor().getProfile().getNickname())

                        // Author profile image
                        .imageDTO(ImageMapper.toDto(
                                post.getAuthor().getProfile().getImageUrl(),
                                post.getAuthor().getProfile().getImagePositionX(),
                                post.getAuthor().getProfile().getImagePositionY()))
                        .build())
                .community(post.getCommunity() != null
                        ? CommunityMapper.toPreviewDTO(post.getCommunity())
                        : null)
                .visibility(post.getVisibility().toString())
                .contentImageDTO(ImageMapper.toDto(post.getImageUrl(), null, null))
                .likedByMe(post.getLikes().stream().anyMatch(like -> like.getUser().equals(viewer)))
                .likeCount(post.getLikes().size())

                // Like users
                .likeUsers(post.getLikes().stream()
                        .map(like -> {
                            User u = like.getUser();
                            // users' profile
                            return LikeUserDTO.builder()
                                    .username(u.getUsername())
                                    .nickname(u.getProfile().getNickname())
                                    .imageDTO(ImageMapper.toDto(
                                            u.getProfile().getImageUrl(),
                                            u.getProfile().getImagePositionX(),
                                            u.getProfile().getImagePositionY()))
                                    .build();
                        }).toList())
                .commentCount(post.getComments().size())
                .comments(CommentMapper.toResponseList(post.getComments()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }


}
