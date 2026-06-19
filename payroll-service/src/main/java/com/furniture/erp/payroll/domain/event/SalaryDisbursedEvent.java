package com.furniture.erp.payroll.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class SalaryDisbursedEvent implements DomainEvent<SalaryDisbursedEvent> {
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public SalaryDisbursedEvent() {}

    public SalaryDisbursedEvent(UUID eventId, UUID aggregateId, Instant timestamp) {
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getAggregateId() { return aggregateId; }
    public Instant getTimestamp() { return timestamp; }

    public static SalaryDisbursedEvent create(UUID aggregateId) {
        return new SalaryDisbursedEvent(UUID.randomUUID(), aggregateId, Instant.now());
    }
}
