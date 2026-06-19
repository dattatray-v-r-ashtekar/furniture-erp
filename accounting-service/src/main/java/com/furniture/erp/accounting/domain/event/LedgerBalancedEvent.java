package com.furniture.erp.accounting.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class LedgerBalancedEvent implements DomainEvent<LedgerBalancedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public LedgerBalancedEvent() {}

    public LedgerBalancedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static LedgerBalancedEvent create(UUID aggregateId) {
        return new LedgerBalancedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
