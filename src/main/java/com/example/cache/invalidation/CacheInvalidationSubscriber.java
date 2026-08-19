package com.example.cache.invalidation;

import com.example.cache.cache.LocalUserCache;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class CacheInvalidationSubscriber implements MessageListener {

    private final ObjectMapper objectMapper;
    private final LocalUserCache localUserCache;

    public CacheInvalidationSubscriber(ObjectMapper objectMapper, LocalUserCache localUserCache) {
        this.objectMapper = objectMapper;
        this.localUserCache = localUserCache;
    }

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
           String payload = new String(message.getBody(), StandardCharsets.UTF_8);
           CacheInvalidationEvent event = objectMapper.readValue(payload, CacheInvalidationEvent.class);
           System.out.println("Received Invalidation : " + event);
           if ("USER".equals(event.entityType())) {
               localUserCache.delete(event.entityId());
           }
        } catch(Exception e) {
            System.out.println("Failed to precess invalidation : " + e.getMessage());
        }
    }

}
