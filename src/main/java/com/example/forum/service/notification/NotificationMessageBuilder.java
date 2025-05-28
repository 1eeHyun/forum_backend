package com.example.forum.service.notification;

import com.example.forum.model.comment.Comment;
import com.example.forum.model.post.Post;

public class NotificationMessageBuilder {

    public static String buildCommentNotification(String senderNickname, Post post) {
        return senderNickname + " commented on your post.";
    }

    public static String buildReplyNotification(String senderNickname, Comment parentComment) {
        return senderNickname + " replied to your comment.";
    }

    public static String buildPostLikeNotification(String senderNickname, Post post) {
        return senderNickname + " liked your post.";
    }

    public static String buildCommentLikeNotification(String senderNickname, Comment comment) {
        return senderNickname + " liked your comment.";
    }

    public static String buildFollowNotification(String senderNickname) {
        return senderNickname + " is now following you.";
    }
}
