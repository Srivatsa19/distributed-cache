package com.example.cache.invalidation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RedisStreamInfrastructureConfig {

    @Bean
    public String cacheInvalidationGroup(StringRedisTemplate redisTemplate, @Value("${cache.instance-id}") String instanceId) {
        String groupName = RedisStreamConstants.GROUP_PREFIX + instanceId;
        boolean groupExists = redisTemplate.opsForStream()
                        .groups(RedisStreamConstants.STREAM)
                        .stream()
                        .anyMatch(group -> groupName.equals(group.groupName()));
        if (!groupExists) {
            redisTemplate.opsForStream().createGroup(RedisStreamConstants.STREAM, groupName);
            System.out.println("Created consumer group: " + groupName);
        } else {
            System.out.println("Consumer group already exists: " + groupName);
        }
        return groupName;
    }

}
