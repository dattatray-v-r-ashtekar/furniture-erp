package com.furniture.erp.procurement.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "purchase_orders")
public class PurchaseOrder extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String vendorId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "purchase_order_id")
    private List<PurchaseOrderLine> lines = new ArrayList<>();

    protected PurchaseOrder() {
    }

    public PurchaseOrder(String vendorId) {
        this.id = UUID.randomUUID();
        this.vendorId = vendorId;
        this.status = OrderStatus.DRAFT;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getVendorId() { return vendorId; }
    public OrderStatus getStatus() { return status; }
    public List<PurchaseOrderLine> getLines() { return lines; }

    public void addLine(PurchaseOrderLine line) {
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Can only add lines to DRAFT orders");
        }
        this.lines.add(line);
    }

    public void issueOrder() {
        if (this.status != OrderStatus.DRAFT) {
            throw new IllegalStateException("Only DRAFT orders can be issued");
        }
        if (this.lines.isEmpty()) {
            throw new IllegalStateException("Cannot issue an empty order");
        }
        this.status = OrderStatus.ISSUED;
    }

    public void markReceived() {
        if (this.status != OrderStatus.ISSUED) {
            throw new IllegalStateException("Only ISSUED orders can be received");
        }
        this.status = OrderStatus.RECEIVED;
    }
}
