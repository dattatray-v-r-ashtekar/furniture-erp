package com.furniture.erp.erpcentral.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sales_orders")
public class SalesOrder extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sales_order_id")
    private List<SalesOrderLine> items = new ArrayList<>();

    protected SalesOrder() {
    }

    public SalesOrder(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<SalesOrderLine> getItems() { return items; }

    public void addItem(SalesOrderLine item) {
        this.items.add(item);
    }
}
