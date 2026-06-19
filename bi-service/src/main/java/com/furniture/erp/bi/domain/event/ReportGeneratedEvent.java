package com.furniture.erp.bi.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ReportGeneratedEvent implements DomainEvent<ReportGeneratedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public ReportGeneratedEvent() {}

    public ReportGeneratedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static ReportGeneratedEvent create(UUID aggregateId) {
        return new ReportGeneratedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
