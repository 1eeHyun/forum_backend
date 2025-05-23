package com.example.forum.service.common;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RecentViewService {

    private final RedisTemplate<String, String> redisTemplate;
    private static final int MAX_SIZE = 10;

    public void addPostView(Long userId, Long postId) {

        String key = "recent:view:" + userId;

        // Remove duplicated
        redisTemplate.opsForList().remove(key, 0, postId.toString());

        // AddFirst new post
        redisTemplate.opsForList().leftPush(key, postId.toString());

        // Limit 10
        redisTemplate.opsForList().trim(key, 0, MAX_SIZE - 1);
    }


    public List<Long> getRecentPostIds(Long userId) {

        String key = "recent:view:" + userId;
        List<String> values = redisTemplate.opsForList().range(key, 0, -1);
        return values.stream().map(Long::parseLong).toList();
    }
}
