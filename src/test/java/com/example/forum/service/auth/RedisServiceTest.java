package com.example.forum.service.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RedisServiceTest {

    @InjectMocks
    private RedisService redisService;

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("markUserOnline - sets key with TTL")
    void markUserOnline_success() {
        Long userId = 123L;
        redisService.markUserOnline(userId);

        verify(valueOperations).set(eq("user:online:" + userId), eq("true"), any());
    }

    @Test
    @DisplayName("markUserOffline - deletes key")
    void markUserOffline_success() {
        Long userId = 456L;
        redisService.markUserOffline(userId);

        verify(redisTemplate).delete("user:online:" + userId);
    }

    @Test
    @DisplayName("isUserOnline - returns true when key exists")
    void isUserOnline_true() {
        Long userId = 789L;
        when(redisTemplate.hasKey("user:online:" + userId)).thenReturn(true);

        boolean result = redisService.isUserOnline(userId);

        assertTrue(result);
    }

    @Test
    @DisplayName("isUserOnline - returns false when key does not exist")
    void isUserOnline_false() {
        Long userId = 101L;
        when(redisTemplate.hasKey("user:online:" + userId)).thenReturn(false);

        boolean result = redisService.isUserOnline(userId);

        assertFalse(result);
    }

    @Test
    @DisplayName("getOnlineUserKeys - returns all keys matching pattern")
    void getOnlineUserKeys_success() {
        Set<String> keys = Set.of("user:online:1", "user:online:2");
        when(redisTemplate.keys("user:online:*")).thenReturn(keys);

        Set<String> result = redisService.getOnlineUserKeys();

        assertEquals(keys, result);
    }

    @Test
    @DisplayName("extractUserIdFromKey - parses user ID correctly")
    void extractUserIdFromKey_success() {
        String key = "user:online:42";

        Long result = redisService.extractUserIdFromKey(key);

        assertEquals(42L, result);
    }
}
