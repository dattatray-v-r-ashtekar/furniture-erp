package com.furniture.erp.wms.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class BinCapacityReachedEvent implements DomainEvent<BinCapacityReachedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public BinCapacityReachedEvent() {}

    public BinCapacityReachedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static BinCapacityReachedEvent create(UUID aggregateId) {
        return new BinCapacityReachedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
