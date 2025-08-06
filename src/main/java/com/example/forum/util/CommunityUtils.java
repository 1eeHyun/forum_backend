package com.example.forum.util;

import com.example.forum.model.community.Community;

import java.util.Set;

public class CommunityUtils {


    private CommunityUtils() {}

    public static boolean isFavorite(Community community, Set<Long> favoriteCommunityIds) {
        return community != null && favoriteCommunityIds.contains(community.getId());
    }
}
