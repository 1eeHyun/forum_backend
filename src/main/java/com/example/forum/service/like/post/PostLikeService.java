package com.example.forum.service.like.post;

import com.example.forum.dto.like.LikeUserDTO;

import java.util.List;

public interface PostLikeService {

    void toggleLike(Long postId, String username);

    List<LikeUserDTO> getLikeUsers(Long postId);

    long countLikes(Long postId);
}
