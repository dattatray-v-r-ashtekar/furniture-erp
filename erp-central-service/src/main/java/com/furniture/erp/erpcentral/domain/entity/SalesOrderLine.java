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
    private String description;

    protected SalesOrderLine() {
    }

    public SalesOrderLine(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
