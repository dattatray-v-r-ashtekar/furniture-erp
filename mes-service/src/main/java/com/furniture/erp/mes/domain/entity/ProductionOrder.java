package com.furniture.erp.mes.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "production_orders")
public class ProductionOrder extends AggregateRoot<UUID> {

    @Id
    private UUID id;
    private String productSku;
    private Integer targetQuantity;

    @Enumerated(EnumType.STRING)
    private ProductionStatus status;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "production_order_id")
    private List<WorkOrder> workOrders = new ArrayList<>();

    protected ProductionOrder() {
    }

    public ProductionOrder(String productSku, Integer targetQuantity) {
        this.id = UUID.randomUUID();
        this.productSku = productSku;
        this.targetQuantity = targetQuantity;
        this.status = ProductionStatus.PLANNED;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getProductSku() { return productSku; }
    public Integer getTargetQuantity() { return targetQuantity; }
    public ProductionStatus getStatus() { return status; }
    public List<WorkOrder> getWorkOrders() { return workOrders; }

    public void addWorkOrder(WorkOrder workOrder) {
        if (this.status != ProductionStatus.PLANNED) {
            throw new IllegalStateException("Can only add work orders to PLANNED production orders");
        }
        this.workOrders.add(workOrder);
    }

    public void startProduction() {
        if (this.status != ProductionStatus.PLANNED) {
            throw new IllegalStateException("Only PLANNED production orders can be started");
        }
        if (this.workOrders.isEmpty()) {
            throw new IllegalStateException("Cannot start production without work orders");
        }
        this.status = ProductionStatus.IN_PROGRESS;
    }

    public void completeProduction() {
        if (this.status != ProductionStatus.IN_PROGRESS) {
            throw new IllegalStateException("Only IN_PROGRESS production orders can be completed");
        }
        
        boolean allDone = this.workOrders.stream()
                .allMatch(wo -> wo.getStatus() == WorkOrderStatus.DONE);
                
        if (!allDone) {
            throw new IllegalStateException("Cannot complete production until all work orders are DONE");
        }
        
        this.status = ProductionStatus.COMPLETED;
    }

    public WorkOrder getWorkOrder(UUID workOrderId) {
        return this.workOrders.stream()
                .filter(wo -> wo.getId().equals(workOrderId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Work order not found: " + workOrderId));
    }
}
