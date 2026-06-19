package com.furniture.erp.qms.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class InspectionFailedEvent implements DomainEvent<InspectionFailedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public InspectionFailedEvent() {}

    public InspectionFailedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static InspectionFailedEvent create(UUID aggregateId) {
        return new InspectionFailedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
