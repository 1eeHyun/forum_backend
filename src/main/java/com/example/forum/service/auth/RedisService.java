package com.example.forum.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(5);

    public void markUserOnline(Long userId) {
        String key = "user:online:" + userId;
        redisTemplate.opsForValue().set(key, "true", TTL);
    }

    public void markUserOffline(Long userId) {
        String key = "user:online:" + userId;
        redisTemplate.delete(key);
    }

    public boolean isUserOnline(Long userId) {
        String key = "user:online:" + userId;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    public Set<String> getOnlineUserKeys() {
        return redisTemplate.keys("user:online:*");
    }

    public Long extractUserIdFromKey(String key) {
        return Long.parseLong(key.replace("user:online:", ""));
    }

}
