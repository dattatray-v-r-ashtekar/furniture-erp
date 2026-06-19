package com.furniture.erp.crm.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class CustomerConvertedEvent implements DomainEvent<CustomerConvertedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public CustomerConvertedEvent() {}

    public CustomerConvertedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static CustomerConvertedEvent create(UUID aggregateId) {
        return new CustomerConvertedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
