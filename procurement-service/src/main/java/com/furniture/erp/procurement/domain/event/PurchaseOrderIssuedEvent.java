package com.furniture.erp.procurement.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class PurchaseOrderIssuedEvent implements DomainEvent<PurchaseOrderIssuedEvent> {
    private UUID eventId;
    private UUID purchaseOrderId;
    private String vendorId;
    private Instant timestamp;

    public PurchaseOrderIssuedEvent() {}

    public PurchaseOrderIssuedEvent(UUID eventId, UUID purchaseOrderId, String vendorId, Instant timestamp) {
        this.eventId = eventId;
        this.purchaseOrderId = purchaseOrderId;
        this.vendorId = vendorId;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getPurchaseOrderId() { return purchaseOrderId; }
    public String getVendorId() { return vendorId; }
    public Instant getTimestamp() { return timestamp; }

    public static PurchaseOrderIssuedEvent create(UUID purchaseOrderId, String vendorId) {
        return new PurchaseOrderIssuedEvent(UUID.randomUUID(), purchaseOrderId, vendorId, Instant.now());
    }
}
