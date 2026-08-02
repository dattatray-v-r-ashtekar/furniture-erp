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
    private Double totalAmount;
    private String status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "sales_order_id")
    private List<SalesOrderLine> items = new ArrayList<>();

    protected SalesOrder() {
    }

    public SalesOrder(UUID id, String referenceCode, Double totalAmount, String status) {
        this.id = id != null ? id : UUID.randomUUID();
        this.referenceCode = referenceCode;
        this.totalAmount = totalAmount != null ? totalAmount : 0.0;
        this.status = status != null ? status : "CONFIRMED";
        super.setId(this.id);
    }

    public SalesOrder(String referenceCode) {
        this(UUID.randomUUID(), referenceCode, 0.0, "CONFIRMED");
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public Double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public List<SalesOrderLine> getItems() { return items; }

    public void addItem(SalesOrderLine item) {
        this.items.add(item);
    }
}
