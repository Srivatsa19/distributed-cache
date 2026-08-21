package com.example.cache.outbox;

import com.example.cache.invalidation.RedisStreamConstants;
import com.example.cache.invalidation.RedisStreamPublisher;
import com.example.cache.model.OutboxEvent;
import com.example.cache.repository.OutboxRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class OutboxPublisher {

    private final OutboxRepository outboxRepository;
    private final String publisherId;
    private final RedisStreamPublisher redisStreamPublisher;
    private final int batchSize;
    private final int leaseSeconds;

    public OutboxPublisher(OutboxRepository outboxRepository, @Value("${cache.instance-id}") String publisherId, RedisStreamPublisher redisStreamPublisher, @Value("${outbox.batch-size}") int batchSize, @Value("${outbox.claim-lease-seconds}") int leaseSeconds) {
        this.outboxRepository = outboxRepository;
        this.publisherId = publisherId;
        this.redisStreamPublisher = redisStreamPublisher;
        this.batchSize = batchSize;
        this.leaseSeconds = leaseSeconds;
    }

    @Scheduled(fixedDelayString = "${outbox.poll-interval-ms}")
    public void publishPendingEvents() {
        var events = outboxRepository.claimBatch(publisherId, batchSize, leaseSeconds);
        for (OutboxEvent event : events) {
            String streamId = redisStreamPublisher.publish(event);
            outboxRepository.markPublished(event.id(), publisherId);
        }
    }

}
