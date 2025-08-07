package com.example.forum.service.reaction.comment;

public interface CommentLikeService {

    void toggleLike(Long commentId, String username);
    void toggleDislike(Long commentId, String username);

    long countLikes(Long commentId);
    long countDislikes(Long commentId);

    boolean hasUserLiked(Long commentId, String username);
    boolean hasUserDisliked(Long commentId, String username);
}
