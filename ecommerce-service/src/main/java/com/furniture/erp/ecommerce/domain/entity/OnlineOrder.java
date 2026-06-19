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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "online_order_id")
    private List<CartItem> items = new ArrayList<>();

    protected OnlineOrder() {
    }

    public OnlineOrder(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<CartItem> getItems() { return items; }

    public void addItem(CartItem item) {
        this.items.add(item);
    }
}
