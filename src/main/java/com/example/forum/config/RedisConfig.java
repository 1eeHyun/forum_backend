package com.example.forum.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, String> tpl = new RedisTemplate<>();
        StringRedisSerializer s = new StringRedisSerializer();
        tpl.setConnectionFactory(cf);
        tpl.setKeySerializer(s);
        tpl.setValueSerializer(s);
        tpl.setHashKeySerializer(s);
        tpl.setHashValueSerializer(s);
        tpl.afterPropertiesSet();
        return tpl;
    }
}
