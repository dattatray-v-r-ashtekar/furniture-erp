package com.furniture.erp.dealerportal.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "wholesale_orders")
public class WholesaleOrder extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "wholesale_order_id")
    private List<WholesaleItem> items = new ArrayList<>();

    protected WholesaleOrder() {
    }

    public WholesaleOrder(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<WholesaleItem> getItems() { return items; }

    public void addItem(WholesaleItem item) {
        this.items.add(item);
    }
}
