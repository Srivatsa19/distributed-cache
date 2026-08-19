package com.example.cache.invalidation;

public record CacheInvalidationEvent(
        String entityType,
        long entityId
) {
}
