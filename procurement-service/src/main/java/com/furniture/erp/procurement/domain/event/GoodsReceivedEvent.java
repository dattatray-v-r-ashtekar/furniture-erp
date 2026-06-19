package com.furniture.erp.procurement.domain.event;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.procurement.domain.entity.PurchaseOrderLine;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GoodsReceivedEvent implements DomainEvent<GoodsReceivedEvent> {
    private UUID eventId;
    private UUID purchaseOrderId;
    private List<ReceivedLine> lines;
    private Instant timestamp;

    public GoodsReceivedEvent() {}

    public GoodsReceivedEvent(UUID eventId, UUID purchaseOrderId, List<ReceivedLine> lines, Instant timestamp) {
        this.eventId = eventId;
        this.purchaseOrderId = purchaseOrderId;
        this.lines = lines;
        this.timestamp = timestamp;
    }

    public UUID getEventId() { return eventId; }
    public UUID getPurchaseOrderId() { return purchaseOrderId; }
    public List<ReceivedLine> getLines() { return lines; }
    public Instant getTimestamp() { return timestamp; }

    public static GoodsReceivedEvent create(UUID purchaseOrderId, List<PurchaseOrderLine> poLines) {
        List<ReceivedLine> receivedLines = new ArrayList<>();
        for (PurchaseOrderLine line : poLines) {
            receivedLines.add(new ReceivedLine(line.getSkuCode(), line.getQuantity()));
        }
        return new GoodsReceivedEvent(UUID.randomUUID(), purchaseOrderId, receivedLines, Instant.now());
    }

    public static class ReceivedLine {
        private String skuCode;
        private Integer quantity;

        public ReceivedLine() {}

        public ReceivedLine(String skuCode, Integer quantity) {
            this.skuCode = skuCode;
            this.quantity = quantity;
        }

        public String getSkuCode() { return skuCode; }
        public Integer getQuantity() { return quantity; }
    }
}
