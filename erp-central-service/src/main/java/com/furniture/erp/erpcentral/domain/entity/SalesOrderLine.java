package com.furniture.erp.erpcentral.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "sales_order_lines")
public class SalesOrderLine extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String sku;
    private Integer quantity;
    private Double price;
    private String description;

    protected SalesOrderLine() {
    }

    public SalesOrderLine(String sku, String description, Integer quantity, Double price) {
        this.id = UUID.randomUUID();
        this.sku = sku;
        this.description = description;
        this.quantity = quantity != null ? quantity : 1;
        this.price = price != null ? price : 0.0;
        super.setId(this.id);
    }

    public SalesOrderLine(String description) {
        this("ITEM", description, 1, 0.0);
    }

    public UUID getId() { return id; }
    public String getSku() { return sku; }
    public String getDescription() { return description; }
    public Integer getQuantity() { return quantity; }
    public Double getPrice() { return price; }
}
