package com.example.forum.service.follow;

public interface FollowService {

    void followToggle(String targetUsername, String currentUsername);
    boolean isFollowing(String targetUsername, String currentUsername);
}
