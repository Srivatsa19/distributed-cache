package com.example.cache.invalidation;

import com.example.cache.cache.LocalUserCache;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class CacheInvalidationStreamConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    private final LocalUserCache localUserCache;
    private final StringRedisTemplate redisTemplate;
    private final String groupName;

    public CacheInvalidationStreamConsumer(LocalUserCache localUserCache, StringRedisTemplate redisTemplate, String cacheInvalidationGroup) {
        this.localUserCache = localUserCache;
        this.redisTemplate = redisTemplate;
        this.groupName = cacheInvalidationGroup;
    }

    @Override
    public void onMessage(MapRecord<String, String, String> message) {
        String eventId = message.getValue().get("eventId");
        String entityType = message.getValue().get("entityType");
        String entityId = message.getValue().get("entityId");
        System.out.println("Processing eventId=" + eventId + ", redisId=" + message.getId());
        if (Objects.equals("USER", entityType)) {
            long userId = Long.parseLong(entityId);
            localUserCache.delete(userId);
            redisTemplate.opsForStream().acknowledge( RedisStreamConstants.STREAM, groupName, message.getId());
            System.out.println("ACKed event : " + message.getId());
        }
    }

}
