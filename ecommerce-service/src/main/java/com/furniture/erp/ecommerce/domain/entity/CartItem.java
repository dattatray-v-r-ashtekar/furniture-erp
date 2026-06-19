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
    private String description;

    protected CartItem() {
    }

    public CartItem(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
