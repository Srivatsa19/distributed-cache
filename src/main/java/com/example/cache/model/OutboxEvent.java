package com.example.cache.model;

import com.example.cache.enums.OutboxStatus;

import java.time.LocalDateTime;

public record OutboxEvent(
        Long id,
        String eventType,
        String entityType,
        Long entityId,
        String payload,
        LocalDateTime createdAt,
        LocalDateTime publishedAt,
        OutboxStatus status,
        String claimedBy,
        LocalDateTime claimedAt
) {
}
