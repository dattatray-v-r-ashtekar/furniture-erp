package com.furniture.erp.mes.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ProductionOrderCreatedEvent implements DomainEvent<ProductionOrderCreatedEvent> {
    private UUID eventId;
    private UUID productionOrderId;
    private String productSku;
    private Integer targetQuantity;
    private Instant timestamp;

    public ProductionOrderCreatedEvent() {}

    public ProductionOrderCreatedEvent(UUID eventId, UUID productionOrderId, String productSku, Integer targetQuantity, Instant timestamp) {
        this.eventId = eventId;
        this.productionOrderId = productionOrderId;
        this.productSku = productSku;
        this.targetQuantity = targetQuantity;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getProductionOrderId() { return productionOrderId; }
    public String getProductSku() { return productSku; }
    public Integer getTargetQuantity() { return targetQuantity; }
    public Instant getTimestamp() { return timestamp; }

    public static ProductionOrderCreatedEvent create(UUID productionOrderId, String productSku, Integer targetQuantity) {
        return new ProductionOrderCreatedEvent(UUID.randomUUID(), productionOrderId, productSku, targetQuantity, Instant.now());
    }
}
