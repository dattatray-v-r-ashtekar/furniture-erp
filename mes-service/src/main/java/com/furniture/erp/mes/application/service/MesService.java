package com.furniture.erp.mes.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import com.furniture.erp.mes.domain.entity.WorkOrder;
import com.furniture.erp.mes.domain.event.MaterialConsumptionRequestedEvent;
import com.furniture.erp.mes.domain.event.ProductionCompletedEvent;
import com.furniture.erp.mes.domain.event.ProductionOrderCreatedEvent;
import com.furniture.erp.mes.infrastructure.repository.ProductionOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class MesService {

    private final ProductionOrderRepository productionOrderRepository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public MesService(ProductionOrderRepository productionOrderRepository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.productionOrderRepository = productionOrderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ProductionOrder planProduction(String productSku, Integer targetQuantity) {
        ProductionOrder order = new ProductionOrder(productSku, targetQuantity);
        
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
        workOrder.complete();
        productionOrderRepository.save(order);
    }

    @Transactional
    public void completeProductionOrder(UUID orderId) {
        ProductionOrder order = productionOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Production Order not found: " + orderId));
        
        order.completeProduction();
        productionOrderRepository.save(order);

        int totalGoodQty = order.getWorkOrders().get(order.getWorkOrders().size() - 1).getCompletedQuantity();
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
