package com.furniture.erp.mes.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class MaterialConsumptionRequestedEvent implements DomainEvent<MaterialConsumptionRequestedEvent> {
    private UUID eventId;
    private UUID workOrderId;
    private String operationName;
    private Instant timestamp;

    public MaterialConsumptionRequestedEvent() {}

    public MaterialConsumptionRequestedEvent(UUID eventId, UUID workOrderId, String operationName, Instant timestamp) {
        this.eventId = eventId;
        this.workOrderId = workOrderId;
        this.operationName = operationName;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getWorkOrderId() { return workOrderId; }
    public String getOperationName() { return operationName; }
    public Instant getTimestamp() { return timestamp; }

    public static MaterialConsumptionRequestedEvent create(UUID workOrderId, String operationName) {
        return new MaterialConsumptionRequestedEvent(UUID.randomUUID(), workOrderId, operationName, Instant.now());
    }
}
