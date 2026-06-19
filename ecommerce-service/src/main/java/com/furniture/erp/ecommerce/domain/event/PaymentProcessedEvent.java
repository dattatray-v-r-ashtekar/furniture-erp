package com.furniture.erp.ecommerce.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class PaymentProcessedEvent implements DomainEvent<PaymentProcessedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public PaymentProcessedEvent() {}

    public PaymentProcessedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static PaymentProcessedEvent create(UUID aggregateId) {
        return new PaymentProcessedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
