package com.furniture.erp.ecommerce.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "cart_items")
public class CartItem extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String sku;
    private String name;
    private Integer quantity;
    private Double price;
    private String description;

    protected CartItem() {
    }

    public CartItem(String sku, String name, Integer quantity, Double price) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.name = name;
        this.quantity = quantity != null ? quantity : 1;
        this.price = price != null ? price : 0.0;
        this.description = name + " (SKU: " + sku + ", Qty: " + this.quantity + ")";
        super.setId(this.id);
    }

    public CartItem(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.sku = "ITEM";
        this.name = description;
        this.quantity = 1;
        this.price = 0.0;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getName() { return name; }
    public Integer getQuantity() { return quantity; }
    public Double getPrice() { return price; }
    public String getDescription() { return description; }
}
