package com.example.cache.invalidation;

import com.example.cache.model.OutboxEvent;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class RedisStreamPublisher {

    private final StringRedisTemplate redisTemplate;

    public RedisStreamPublisher(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String publish(OutboxEvent event) {
        Map<String, String> fields = Map.of(
                "eventId", String.valueOf(event.id()),
                "eventType", event.eventType(),
                "entityType", event.entityType(),
                "entityId", String.valueOf(event.entityId()),
                "payload", event.payload()
        );
        String streamId = redisTemplate.opsForStream().add(MapRecord.create(RedisStreamConstants.STREAM, fields)).getValue();
        System.out.println("Published outbox event " + event.id() + " -> Redis stream " + streamId);
        return streamId;
    }

}
