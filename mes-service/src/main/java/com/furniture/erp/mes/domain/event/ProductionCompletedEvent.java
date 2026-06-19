package com.furniture.erp.mes.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class ProductionCompletedEvent implements DomainEvent<ProductionCompletedEvent> {
    private UUID eventId;
    private UUID productionOrderId;
    private String productSku;
    private Integer goodQuantity;
    private Integer defectiveQuantity;
    private Instant timestamp;

    public ProductionCompletedEvent() {}

    public ProductionCompletedEvent(UUID eventId, UUID productionOrderId, String productSku, Integer goodQuantity, Integer defectiveQuantity, Instant timestamp) {
        this.eventId = eventId;
        this.productionOrderId = productionOrderId;
        this.productSku = productSku;
        this.goodQuantity = goodQuantity;
        this.defectiveQuantity = defectiveQuantity;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getProductionOrderId() { return productionOrderId; }
    public String getProductSku() { return productSku; }
    public Integer getGoodQuantity() { return goodQuantity; }
    public Integer getDefectiveQuantity() { return defectiveQuantity; }
    public Instant getTimestamp() { return timestamp; }

    public static ProductionCompletedEvent create(UUID productionOrderId, String productSku, Integer goodQuantity, Integer defectiveQuantity) {
        return new ProductionCompletedEvent(UUID.randomUUID(), productionOrderId, productSku, goodQuantity, defectiveQuantity, Instant.now());
    }
}
