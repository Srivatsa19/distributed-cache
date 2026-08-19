package com.example.cache.invalidation;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class CacheInvalidationPublisher {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    public CacheInvalidationPublisher(RedisTemplate<String, String> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishUserInvalidation(long userId) {

        CacheInvalidationEvent event = new CacheInvalidationEvent("USER", userId);

        try {
            String payload = objectMapper.writeValueAsString(event);
            redisTemplate.convertAndSend(RedisPubSubConfig.CHANNEL, payload);
            System.out.println("Published invalidation: " + payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize invalidation event",e);
        }

    }
}
