package com.furniture.erp.dealerportal.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class BulkDiscountAppliedEvent implements DomainEvent<BulkDiscountAppliedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public BulkDiscountAppliedEvent() {}

    public BulkDiscountAppliedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static BulkDiscountAppliedEvent create(UUID aggregateId) {
        return new BulkDiscountAppliedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
