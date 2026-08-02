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
    private String deliveryAddress;
    private String salesOrderId;
    private String trackingNumber;
    private String status;

    protected DeliveryStop() {
    }

    public DeliveryStop(String description) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.deliveryAddress = "Customer Delivery Destination";
        this.salesOrderId = "SO-GEN";
        this.trackingNumber = "FX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = "PENDING";
        super.setId(this.id);
    }

    public DeliveryStop(String description, String deliveryAddress, String salesOrderId, String trackingNumber) {
        this.id = UUID.randomUUID();
        this.description = description;
        this.deliveryAddress = deliveryAddress != null ? deliveryAddress : "Customer Delivery Destination";
        this.salesOrderId = salesOrderId != null ? salesOrderId : "SO-GEN";
        this.trackingNumber = trackingNumber != null ? trackingNumber : "FX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.status = "PENDING";
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getDescription() { return description; }
    public String getDeliveryAddress() { return deliveryAddress; }
    public String getSalesOrderId() { return salesOrderId; }
    public String getTrackingNumber() { return trackingNumber; }
    public String getStatus() { return status; }

    public void setDescription(String description) { this.description = description; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
    public void setSalesOrderId(String salesOrderId) { this.salesOrderId = salesOrderId; }
    public void setTrackingNumber(String trackingNumber) { this.trackingNumber = trackingNumber; }
    public void setStatus(String status) { this.status = status; }

    public void complete() {
        this.status = "COMPLETED";
    }
}
