package com.example.forum.mapper.post;

import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.dto.post.PostDetailDTO;
import com.example.forum.dto.post.PostFileDTO;
import com.example.forum.dto.post.PostPreviewDTO;
import com.example.forum.dto.post.PostResponseDTO;
import com.example.forum.mapper.comment.CommentMapper;
import com.example.forum.mapper.community.CommunityMapper;
import com.example.forum.mapper.image.ImageMapper;
import com.example.forum.mapper.user.UserMapper;
import com.example.forum.model.community.Community;
import com.example.forum.model.like.PostReaction;
import com.example.forum.model.post.Post;
import com.example.forum.model.post.PostFile;
import com.example.forum.model.profile.Profile;
import com.example.forum.model.user.User;
import com.example.forum.util.CommunityUtils;

import java.util.List;
import java.util.Set;

import static com.example.forum.common.TimeUtils.formatTimeAgo;

public class PostMapper {

    public static PostResponseDTO toPostResponseDTO(Post post, boolean isHidden, boolean isFavoriteCommunity) {
        return PostResponseDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .author(UserMapper.toDtoWithEmail(post.getAuthor()))
                .community(
                        post.getCategory() != null
                                ? CommunityMapper.toPreviewDTO(post.getCategory().getCommunity(), isFavoriteCommunity)
                                : null
                )
                .fileUrls(post.getFiles() != null
                            ? convertFileUrls(post.getFiles())
                            : null)
                .commentCount(post.getComments() != null ? post.getComments().size() : 0)
                .likeCount(post.getLikes() != null ? post.getLikes().size() : 0)
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .isHidden(isHidden)
                .build();
    }

    public static PostDetailDTO toPostDetailDTO(Post post, User viewer, boolean isHidden, boolean isFavoriteCommunity) {
        return PostDetailDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                // Author of the post
                .author(UserMapper.toDtoWithEmail(post.getAuthor()))
                .isAuthor(post.getAuthor().equals(viewer))

                .community(
                        post.getCategory() != null
                                ? CommunityMapper.toPreviewDTO(post.getCategory().getCommunity(), isFavoriteCommunity)
                                : null
                )

                .visibility(post.getVisibility().toString())

                .likedByMe(post.getLikes().stream().anyMatch(like -> like.getUser().equals(viewer)))
                .likeCount(post.getLikes().size())
                .fileUrls(post.getFiles() != null
                        ? convertFileUrls(post.getFiles())
                        : null)

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
                .isHidden(isHidden)
                .build();
    }

    public static LikeUserDTO toLikeUserDTO(PostReaction postLike) {
        User user = postLike.getUser();
        Profile profile = user.getProfile();

        return LikeUserDTO.builder()
                .username(user.getUsername())
                .nickname(profile.getNickname())
                .imageDTO(ImageMapper.profileToImageDto(profile))
                .build();
    }

    public static PostPreviewDTO toPreviewDTO(Post post, boolean isHidden) {

        return PostPreviewDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .fileUrls(
                        convertFileUrls(post.getFiles())
                )
                .likeCount(post.getLikes().size())
                .commentCount(post.getComments().size())
                .createdAtFormatted(formatTimeAgo(post.getCreatedAt()))

                .communityName(
                        post.getCategory() != null
                                ? post.getCategory().getCommunity().getName()
                                : null
                )
                .communityId(
                        post.getCategory() != null
                                ? post.getCategory().getCommunity().getId()
                                : null
                )
                .communityProfilePicture(
                        post.getCategory() != null
                                ? ImageMapper.toDto(
                                post.getCategory().getCommunity().getProfileImageUrl(),
                                post.getCategory().getCommunity().getProfileImagePositionX(),
                                post.getCategory().getCommunity().getProfileImagePositionY()
                        )
                                : null
                )

                .authorNickname(post.getAuthor().getProfile().getNickname())
                .author(UserMapper.toDtoWithEmail(post.getAuthor()))
                .isHidden(isHidden)
                .build();
    }

    private static List<PostFileDTO> convertFileUrls(List<PostFile> files) {

        return files.stream()
                .map(file -> PostFileDTO.builder()
                        .fileUrl(file.getFileUrl())
                        .type(file.getType())
                        .build())
                .toList();
    }

    public static PostResponseDTO toPostResponseDTOWithFlags(Post post, Set<Long> hiddenPostIds, Set<Long> favoriteCommunityIds) {
        boolean isHidden = hiddenPostIds.contains(post.getId());

        Community community = post.getCategory() != null
                ? post.getCategory().getCommunity()
                : null;

        boolean isFavorite = CommunityUtils.isFavorite(community, favoriteCommunityIds);

        return toPostResponseDTO(post, isHidden, isFavorite);
    }
}
