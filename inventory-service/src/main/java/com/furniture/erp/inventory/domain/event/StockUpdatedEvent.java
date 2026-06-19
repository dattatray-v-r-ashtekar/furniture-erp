package com.furniture.erp.inventory.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class StockUpdatedEvent implements DomainEvent<StockUpdatedEvent> {
    private UUID eventId;
    private UUID stockItemId;
    private String skuCode;
    private Integer newQuantity;
    private Instant timestamp;

    public StockUpdatedEvent() {}

    public StockUpdatedEvent(UUID eventId, UUID stockItemId, String skuCode, Integer newQuantity, Instant timestamp) {
        this.eventId = eventId;
        this.stockItemId = stockItemId;
        this.skuCode = skuCode;
        this.newQuantity = newQuantity;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getStockItemId() { return stockItemId; }
    public String getSkuCode() { return skuCode; }
    public Integer getNewQuantity() { return newQuantity; }
    public Instant getTimestamp() { return timestamp; }

    public static StockUpdatedEvent create(UUID stockItemId, String skuCode, Integer newQuantity) {
        return new StockUpdatedEvent(UUID.randomUUID(), stockItemId, skuCode, newQuantity, Instant.now());
    }
}
