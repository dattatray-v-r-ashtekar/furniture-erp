package com.furniture.erp.dealerportal.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "wholesale_items")
public class WholesaleItem extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String description;

    protected WholesaleItem() {
    }

    public WholesaleItem(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
