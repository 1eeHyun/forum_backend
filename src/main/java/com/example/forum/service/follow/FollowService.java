package com.example.forum.service.follow;

public interface FollowService {

    void follow(String targetUsername, String currentUsername);
    void unfollow(String targetUsername, String currentUsername);
    boolean isFollowing(String targetUsername, String currentUsername);
}
