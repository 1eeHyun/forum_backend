package com.example.forum.service.post.hidden;

import com.example.forum.model.post.Post;

import java.util.Set;

public interface HiddenPostService {

    boolean isHiddenByUsername(Post post, String username);
    Set<Long> getHiddenPostIdsByUsername(String username);
}
