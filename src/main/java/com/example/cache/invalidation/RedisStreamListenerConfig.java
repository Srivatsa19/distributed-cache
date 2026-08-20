package com.example.cache.invalidation;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

import java.time.Duration;

@Configuration
public class RedisStreamListenerConfig {

    public static final String STREAM = "cache-invalidation";

    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(
            RedisConnectionFactory connectionFactory,
            CacheInvalidationStreamConsumer consumer,
            String cacheInvalidationGroup,
            @Value("${cache.instance-id}") String instanceId) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer
                        .StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofMillis(500))
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(connectionFactory, options);

        container.receive(
                Consumer.from(cacheInvalidationGroup, instanceId),
                StreamOffset.create(RedisStreamConstants.STREAM, ReadOffset.lastConsumed()),
                consumer
        );
        container.start();
        return container;
    }

}
