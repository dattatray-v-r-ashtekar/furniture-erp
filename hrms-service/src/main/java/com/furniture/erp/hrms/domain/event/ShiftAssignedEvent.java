package com.furniture.erp.hrms.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ShiftAssignedEvent implements DomainEvent<ShiftAssignedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public ShiftAssignedEvent() {}

    public ShiftAssignedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static ShiftAssignedEvent create(UUID aggregateId) {
        return new ShiftAssignedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
