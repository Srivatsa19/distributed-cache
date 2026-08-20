package com.example.cache.invalidation;

import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CacheInvalidationStreamPublisher {

    private final StringRedisTemplate redisTemplate;

    public CacheInvalidationStreamPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void publishUserInvalidation(long userId) {

        Map<String, String> event = Map.of(
                "entityType", "USER", "entityId", String.valueOf(userId)
        );
        MapRecord<String, String, String> record = MapRecord.create(RedisStreamConstants.STREAM, event);
        String recordId = redisTemplate.opsForStream().add(record).getValue();
        System.out.println("Published stream event: " + recordId);

    }

}
