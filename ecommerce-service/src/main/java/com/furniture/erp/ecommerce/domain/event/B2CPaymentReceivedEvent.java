package com.furniture.erp.ecommerce.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class B2CPaymentReceivedEvent implements DomainEvent<B2CPaymentReceivedEvent> {
    private UUID eventId;
    private UUID orderId;
    private String referenceCode;
    private Double totalAmount;
    private List<ItemDto> items;
    private Instant timestamp;

    public B2CPaymentReceivedEvent() {}

    public B2CPaymentReceivedEvent(UUID eventId, UUID orderId, String referenceCode, Double totalAmount, List<ItemDto> items, Instant timestamp) {
        this.eventId = eventId;
        this.orderId = orderId;
        this.referenceCode = referenceCode;
        this.totalAmount = totalAmount;
        this.items = items;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getOrderId() { return orderId; }
    public String getReferenceCode() { return referenceCode; }
    public Double getTotalAmount() { return totalAmount; }
    public List<ItemDto> getItems() { return items; }
    public Instant getTimestamp() { return timestamp; }

    public static B2CPaymentReceivedEvent create(UUID orderId, String referenceCode, Double totalAmount, List<ItemDto> items) {
        return new B2CPaymentReceivedEvent(UUID.randomUUID(), orderId, referenceCode, totalAmount, items, Instant.now());
    }

    public static class ItemDto {
        private String sku;
        private String name;
        private Integer quantity;
        private Double price;

        public ItemDto() {}
        public ItemDto(String sku, String name, Integer quantity, Double price) {
            this.sku = sku;
            this.name = name;
            this.quantity = quantity;
            this.price = price;
        }
        public String getSku() { return sku; }
        public String getName() { return name; }
        public Integer getQuantity() { return quantity; }
        public Double getPrice() { return price; }
    }
}
