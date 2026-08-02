package com.furniture.erp.ecommerce.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "online_orders")
public class OnlineOrder extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;
    private Double totalAmount;
    private String status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "online_order_id")
    private List<CartItem> items = new ArrayList<>();

    protected OnlineOrder() {
    }

    public OnlineOrder(String referenceCode, Double totalAmount) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        this.totalAmount = totalAmount != null ? totalAmount : 0.0;
        this.status = "PAID";
        super.setId(this.id);
    }

    public OnlineOrder(String referenceCode) {
        this(referenceCode, 0.0);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public Double getTotalAmount() { return totalAmount; }
    public String getStatus() { return status; }
    public List<CartItem> getItems() { return items; }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void addItem(CartItem item) {
        this.items.add(item);
    }
}
