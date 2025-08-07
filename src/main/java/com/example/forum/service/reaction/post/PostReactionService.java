package com.example.forum.service.reaction.post;

import com.example.forum.dto.like.LikeUserDTO;
import com.example.forum.model.like.ReactionType;

import java.util.List;

public interface PostReactionService {

    void toggleReaction(Long postId, String username, ReactionType newType);

    List<LikeUserDTO> getLikeUsers(Long postId);

    long countLikes(Long postId);

    ReactionType getMyReaction(Long postId, String username);
}
