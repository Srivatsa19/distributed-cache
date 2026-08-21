package com.example.cache.repository;

import com.example.cache.enums.OutboxStatus;
import com.example.cache.model.OutboxEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class OutboxRepository {

    private final JdbcTemplate jdbcTemplate;

    public OutboxRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void insert(String eventType, String entityType, long entityId, String payload) {
        jdbcTemplate.update(
                """
                INSERT INTO outbox_events (event_type, entity_type, entity_id, payload)
                VALUES (?, ?, ?, ?)
                """,
                eventType,
                entityType,
                entityId,
                payload
        );
    }

    public void markPublished(long eventId, String publisherId) {
        jdbcTemplate.update(
                """
                UPDATE outbox_events
                SET status = 'PUBLISHED',
                    published_at = CURRENT_TIMESTAMP
                WHERE id = ?
                  AND status = 'PROCESSING'
                  AND claimed_by = ?
                """,
                eventId,
                publisherId
        );
    }

    @Transactional
    public List<OutboxEvent> claimBatch(String publisherId, int limit, int leaseSeconds) {

        List<OutboxEvent> events = jdbcTemplate.query(
                """
                SELECT id, event_type, entity_type, entity_id, payload, created_at, published_at,
                    status, claimed_by, claimed_at
                FROM outbox_events
                WHERE
                    status = 'PENDING'
                    OR (
                        status = 'PROCESSING' AND claimed_at < CURRENT_TIMESTAMP - make_interval(secs => ?)
                    )
                ORDER BY id
                LIMIT ?
                FOR UPDATE SKIP LOCKED
                """,
                (rs, rowNum) -> new OutboxEvent(
                        rs.getLong("id"),
                        rs.getString("event_type"),
                        rs.getString("entity_type"),
                        rs.getLong("entity_id"),
                        rs.getString("payload"),
                        rs.getTimestamp("created_at").toLocalDateTime(),
                        rs.getTimestamp("published_at") == null ? null : rs.getTimestamp("published_at").toLocalDateTime(),
                        OutboxStatus.valueOf(rs.getString("status")),
                        rs.getString("claimed_by"),
                        rs.getTimestamp("claimed_at") == null ? null : rs.getTimestamp("claimed_at").toLocalDateTime()
                ),
                leaseSeconds,
                limit
        );

        for (OutboxEvent event : events) {
            jdbcTemplate.update(
                    """
                    UPDATE outbox_events
                    SET status = 'PROCESSING',
                        claimed_by = ?,
                        claimed_at = CURRENT_TIMESTAMP
                    WHERE id = ?
                    """,
                    publisherId,
                    event.id()
            );
        }

        // the events still may contain status as PENDING and claimed_by as null as it was before updating
        return events;
    }

}
