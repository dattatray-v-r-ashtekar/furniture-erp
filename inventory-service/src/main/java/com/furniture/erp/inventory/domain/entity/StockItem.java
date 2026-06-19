package com.furniture.erp.inventory.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "stock_items")
public class StockItem extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String skuCode; // Stock Keeping Unit
    private String description;
    private Integer availableQuantity;
    private String locationBin;

    protected StockItem() {
        // For JPA
    }

    public StockItem(String skuCode, String description, String locationBin) {
        this.id = UUID.randomUUID();
        this.skuCode = skuCode;
        this.description = description;
        this.availableQuantity = 0;
        this.locationBin = locationBin;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getSkuCode() { return skuCode; }
    public String getDescription() { return description; }
    public Integer getAvailableQuantity() { return availableQuantity; }
    public String getLocationBin() { return locationBin; }

    public void addStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to add must be greater than zero");
        }
        this.availableQuantity += quantity;
    }

    public void deductStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity to deduct must be greater than zero");
        }
        if (this.availableQuantity < quantity) {
            throw new IllegalStateException("Insufficient stock for SKU: " + this.skuCode);
        }
        this.availableQuantity -= quantity;
    }
}
