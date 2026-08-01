package com.furniture.erp.procurement.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.procurement.domain.entity.PurchaseOrder;
import com.furniture.erp.procurement.domain.entity.PurchaseOrderLine;
import com.furniture.erp.procurement.domain.event.GoodsReceivedEvent;
import com.furniture.erp.procurement.domain.event.PurchaseOrderIssuedEvent;
import com.furniture.erp.procurement.infrastructure.repository.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class ProcurementService {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public ProcurementService(PurchaseOrderRepository purchaseOrderRepository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public PurchaseOrder createDraftOrder(String vendorId) {
        PurchaseOrder order = new PurchaseOrder(vendorId);
        return purchaseOrderRepository.save(order);
    }

    @Transactional
    public void addLineItem(UUID orderId, String skuCode, Integer quantity, BigDecimal unitPrice) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + orderId));
        
        PurchaseOrderLine line = new PurchaseOrderLine(skuCode, quantity, unitPrice);
        order.addLine(line);
        purchaseOrderRepository.save(order);
    }

    @Transactional
    public void issueOrder(UUID orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + orderId));
        
        order.issueOrder();
        purchaseOrderRepository.save(order);

        eventPublisher.publish(PurchaseOrderIssuedEvent.create(order.getId(), order.getVendorId()));
    }

    @Transactional
    public void receiveGoods(UUID orderId) {
        PurchaseOrder order = purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + orderId));
        
        order.markReceived();
        purchaseOrderRepository.save(order);

        eventPublisher.publish(GoodsReceivedEvent.create(order.getId(), order.getLines()));
    }

    @Transactional(readOnly = true)
    public PurchaseOrder getOrder(UUID orderId) {
        return purchaseOrderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found: " + orderId));
    }

    public java.util.List<PurchaseOrder> getAllOrders() {
        return purchaseOrderRepository.findAll();
    }
}
