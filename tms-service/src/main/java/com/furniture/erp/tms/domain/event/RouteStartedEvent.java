package com.furniture.erp.tms.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class RouteStartedEvent implements DomainEvent<RouteStartedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public RouteStartedEvent() {}

    public RouteStartedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static RouteStartedEvent create(UUID aggregateId) {
        return new RouteStartedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
