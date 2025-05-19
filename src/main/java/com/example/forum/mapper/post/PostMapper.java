package com.example.forum.mapper.post;

import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.auth.AuthorMapper;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.util.ImageMapper;
import com.example.forum.model.like.PostLike;
import com.example.forum.model.post.Post;
import com.example.forum.model.profile.Profile;
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

    public static PostDetailDTO toPostDetailDTO(Post post, User viewer) {
        return PostDetailDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                // Author of the post
                .author(AuthorMapper.toDto(post.getAuthor()))

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

    public static LikeUserDTO toLikeUserDTO(PostLike postLike) {
        User user = postLike.getUser();
        Profile profile = user.getProfile();

        return LikeUserDTO.builder()
                .username(user.getUsername())
                .nickname(profile.getNickname())
                .imageDTO(ImageMapper.profileToImageDto(profile))
                .build();
    }



}
