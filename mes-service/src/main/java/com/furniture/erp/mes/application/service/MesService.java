package com.furniture.erp.mes.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import com.furniture.erp.mes.domain.entity.WorkOrder;
import com.furniture.erp.mes.domain.event.MaterialConsumptionRequestedEvent;
import com.furniture.erp.mes.domain.event.ProductionCompletedEvent;
import com.furniture.erp.mes.domain.event.ProductionOrderCreatedEvent;
import com.furniture.erp.mes.infrastructure.repository.ProductionOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class MesService {

    private static final Logger log = LoggerFactory.getLogger(MesService.class);

    private final ProductionOrderRepository productionOrderRepository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public MesService(ProductionOrderRepository productionOrderRepository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.productionOrderRepository = productionOrderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ProductionOrder planProduction(String productSku, Integer targetQuantity) {
        return planProduction(productSku, targetQuantity, null, null);
    }

    @Transactional
    public ProductionOrder planProduction(String productSku, Integer targetQuantity, String salesOrderId, String orderReference) {
        if (salesOrderId != null && !salesOrderId.isBlank()) {
            List<ProductionOrder> existing = productionOrderRepository.findAll().stream()
                    .filter(po -> salesOrderId.equals(po.getSalesOrderId()) && productSku != null && productSku.equalsIgnoreCase(po.getProductSku()))
                    .toList();
            if (!existing.isEmpty()) {
                log.info("Production order already planned for SalesOrderId {} and SKU {}, returning existing order", salesOrderId, productSku);
                return existing.get(0);
            }
        }

        ProductionOrder order = new ProductionOrder(productSku, targetQuantity, salesOrderId, orderReference);
        
        // Add default routing for standard furniture (just an example)
        order.addWorkOrder(new WorkOrder("Cutting", "Saw-01"));
        order.addWorkOrder(new WorkOrder("Assembly", "Asm-01"));
        order.addWorkOrder(new WorkOrder("Polishing", "Pol-01"));
        
        ProductionOrder savedOrder = productionOrderRepository.save(order);
        
        eventPublisher.publish(ProductionOrderCreatedEvent.create(savedOrder.getId(), productSku, targetQuantity));
        return savedOrder;
    }

    @Transactional
    public void startProductionOrder(UUID orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
        
        order.startProduction();
        productionOrderRepository.save(order);
    }

    @Transactional
    public void startWorkOrder(UUID orderId, UUID workOrderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
        
        WorkOrder workOrder = order.getWorkOrder(workOrderId);
        workOrder.start();
        productionOrderRepository.save(order);

        eventPublisher.publish(MaterialConsumptionRequestedEvent.create(workOrderId, workOrder.getOperationName()));
    }

    @Transactional
    public void reportWorkOrderProgress(UUID orderId, UUID workOrderId, int goodQty, int defectiveQty) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
        
        WorkOrder workOrder = order.getWorkOrder(workOrderId);
        workOrder.reportProgress(goodQty, defectiveQty);
        productionOrderRepository.save(order);
    }

    @Transactional
    public void completeWorkOrder(UUID orderId, UUID workOrderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
        
        WorkOrder workOrder = order.getWorkOrder(workOrderId);
        if (workOrder.getStatus() == com.furniture.erp.mes.domain.entity.WorkOrderStatus.PENDING) {
            workOrder.start();
        }
        if (workOrder.getStatus() == com.furniture.erp.mes.domain.entity.WorkOrderStatus.ACTIVE) {
            int targetQty = (order.getTargetQuantity() != null && order.getTargetQuantity() > 0) ? order.getTargetQuantity() : 1;
            if (workOrder.getCompletedQuantity() == 0) {
                workOrder.reportProgress(targetQty, 0);
            }
            workOrder.complete();
        }
        
        boolean allDone = order.getWorkOrders().stream()
                .allMatch(wo -> wo.getStatus() == com.furniture.erp.mes.domain.entity.WorkOrderStatus.DONE);
        
        if (allDone) {
            if (order.getStatus() == com.furniture.erp.mes.domain.entity.ProductionStatus.PLANNED) {
                order.startProduction();
            }
            if (order.getStatus() == com.furniture.erp.mes.domain.entity.ProductionStatus.IN_PROGRESS) {
                order.completeProduction();
            }
        }
        productionOrderRepository.save(order);

        if (allDone) {
            int targetQty = (order.getTargetQuantity() != null && order.getTargetQuantity() > 0) ? order.getTargetQuantity() : 1;
            int totalGoodQty = Math.max(targetQty, order.getWorkOrders().get(order.getWorkOrders().size() - 1).getCompletedQuantity());
            int totalDefectiveQty = order.getWorkOrders().stream().mapToInt(WorkOrder::getDefectiveQuantity).sum();
            eventPublisher.publish(ProductionCompletedEvent.create(order.getId(), order.getProductSku(), totalGoodQty, totalDefectiveQty));
        }
    }

    @Transactional
    public void completeProductionOrder(UUID orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
        
        if (order.getStatus() == com.furniture.erp.mes.domain.entity.ProductionStatus.PLANNED) {
            order.startProduction();
        }

        int targetQty = (order.getTargetQuantity() != null && order.getTargetQuantity() > 0) ? order.getTargetQuantity() : 1;

        // Ensure work orders are completed before finalizing production
        for (WorkOrder wo : order.getWorkOrders()) {
            if (wo.getStatus() != com.furniture.erp.mes.domain.entity.WorkOrderStatus.DONE) {
                if (wo.getStatus() == com.furniture.erp.mes.domain.entity.WorkOrderStatus.PENDING) {
                    wo.start();
                }
                if (wo.getStatus() == com.furniture.erp.mes.domain.entity.WorkOrderStatus.ACTIVE) {
                    if (wo.getCompletedQuantity() == 0) {
                        wo.reportProgress(targetQty, 0);
                    }
                    wo.complete();
                }
            }
        }

        if (order.getStatus() == com.furniture.erp.mes.domain.entity.ProductionStatus.IN_PROGRESS) {
            order.completeProduction();
        }
        productionOrderRepository.save(order);

        int totalGoodQty = targetQty;
        if (!order.getWorkOrders().isEmpty()) {
            totalGoodQty = Math.max(targetQty, order.getWorkOrders().get(order.getWorkOrders().size() - 1).getCompletedQuantity());
        }
        int totalDefectiveQty = order.getWorkOrders().stream().mapToInt(WorkOrder::getDefectiveQuantity).sum();

        eventPublisher.publish(ProductionCompletedEvent.create(order.getId(), order.getProductSku(), totalGoodQty, totalDefectiveQty));
    }

    @Transactional(readOnly = true)
    public ProductionOrder getOrder(UUID orderId) {
        return productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
    }

    public java.util.List<ProductionOrder> getAllOrders() {
        return productionOrderRepository.findAll();
    }
}
