package com.furniture.erp.tms.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "delivery_stops")
public class DeliveryStop extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String description;

    protected DeliveryStop() {
    }

    public DeliveryStop(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
}
