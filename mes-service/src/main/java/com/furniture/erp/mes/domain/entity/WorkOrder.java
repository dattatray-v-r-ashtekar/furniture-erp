package com.furniture.erp.mes.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "work_orders")
public class WorkOrder extends BaseEntity<UUID> {

    @Id
    private UUID id;
    private String operationName;
    private String machineId;
    
    @Enumerated(EnumType.STRING)
    private WorkOrderStatus status;
    
    private Integer completedQuantity;
    private Integer defectiveQuantity;

    protected WorkOrder() {
    }

    public WorkOrder(String operationName, String machineId) {
        this.id = UUID.randomUUID();
        this.operationName = operationName;
        this.machineId = machineId;
        this.status = WorkOrderStatus.PENDING;
        this.completedQuantity = 0;
        this.defectiveQuantity = 0;
        super.setId(this.id);
    }

    public UUID getId() { return id; }
    public String getOperationName() { return operationName; }
    public String getMachineId() { return machineId; }
    public WorkOrderStatus getStatus() { return status; }
    public Integer getCompletedQuantity() { return completedQuantity; }
    public Integer getDefectiveQuantity() { return defectiveQuantity; }

    public void start() {
        if (this.status != WorkOrderStatus.PENDING) {
            throw new IllegalStateException("Only PENDING work orders can be started");
        }
        this.status = WorkOrderStatus.ACTIVE;
    }

    public void reportProgress(int goodQty, int defectiveQty) {
        if (this.status != WorkOrderStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE work orders can report progress");
        }
        this.completedQuantity += goodQty;
        this.defectiveQuantity += defectiveQty;
    }

    public void complete() {
        if (this.status != WorkOrderStatus.ACTIVE) {
            throw new IllegalStateException("Only ACTIVE work orders can be completed");
        }
        this.status = WorkOrderStatus.DONE;
    }
}
