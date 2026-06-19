package com.furniture.erp.procurement.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "purchase_order_lines")
public class PurchaseOrderLine extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String skuCode;
    private Integer quantity;
    private BigDecimal unitPrice;

    protected PurchaseOrderLine() {
    }

    public PurchaseOrderLine(String skuCode, Integer quantity, BigDecimal unitPrice) {
        this.id = UUID.randomUUID();
        this.skuCode = skuCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getSkuCode() { return skuCode; }
    public Integer getQuantity() { return quantity; }
    public BigDecimal getUnitPrice() { return unitPrice; }
}
