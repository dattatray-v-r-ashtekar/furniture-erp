package com.furniture.erp.tms.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "delivery_routes")
public class DeliveryRoute extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "delivery_route_id")
    private List<DeliveryStop> items = new ArrayList<>();

    protected DeliveryRoute() {
    }

    public DeliveryRoute(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public List<DeliveryStop> getItems() { return items; }

    public void addItem(DeliveryStop item) {
        this.items.add(item);
    }
}
