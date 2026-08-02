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
    private String driverId;
    private String status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "delivery_route_id")
    private List<DeliveryStop> items = new ArrayList<>();

    protected DeliveryRoute() {
    }

    public DeliveryRoute(String referenceCode) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        this.driverId = "DRV-102 (Express Fleet)";
        this.status = "SCHEDULED";
        super.setId(this.id);
    }

    public DeliveryRoute(String referenceCode, String driverId) {
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        this.driverId = driverId != null ? driverId : "DRV-102 (Express Fleet)";
        this.status = "SCHEDULED";
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getReferenceCode() { return referenceCode; }
    public String getDriverId() { return driverId; }
    public String getStatus() { return status; }
    public List<DeliveryStop> getItems() { return items; }
    public List<DeliveryStop> getStops() { return items; }

    public void setDriverId(String driverId) { this.driverId = driverId; }
    public void setStatus(String status) { this.status = status; }

    public void addItem(DeliveryStop item) {
        this.items.add(item);
    }

    public void startRoute() {
        this.status = "IN_TRANSIT";
        for (DeliveryStop stop : this.items) {
            if ("PENDING".equalsIgnoreCase(stop.getStatus())) {
                stop.setStatus("IN_TRANSIT");
            }
        }
    }

    public void completeStop(UUID stopId) {
        for (DeliveryStop stop : this.items) {
            if (stop.getId().equals(stopId)) {
                stop.complete();
            }
        }
        boolean allDone = this.items.stream()
                .allMatch(s -> "COMPLETED".equalsIgnoreCase(s.getStatus()));
        if (allDone && !this.items.isEmpty()) {
            this.status = "COMPLETED";
        }
    }

    public void completeRoute() {
        this.status = "COMPLETED";
        for (DeliveryStop stop : this.items) {
            stop.complete();
        }
    }
}
