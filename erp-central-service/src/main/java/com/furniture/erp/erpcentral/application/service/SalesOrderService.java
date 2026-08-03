package com.furniture.erp.erpcentral.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.erpcentral.domain.entity.SalesOrder;
import com.furniture.erp.erpcentral.domain.entity.SalesOrderLine;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import com.furniture.erp.erpcentral.infrastructure.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class SalesOrderService {

    private final SalesOrderRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public SalesOrderService(SalesOrderRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SalesOrder createSalesOrder(UUID orderId, String referenceCode, Double totalAmount, List<SalesOrderCreatedEvent.ItemDto> items) {
        if (orderId != null && repository.existsById(orderId)) {
            return repository.findById(orderId).get();
        }

        SalesOrder agg = new SalesOrder(orderId, referenceCode, totalAmount, "CONFIRMED");
        
        List<SalesOrderCreatedEvent.ItemDto> eventItems = new ArrayList<>();
        if (items != null && !items.isEmpty()) {
            for (SalesOrderCreatedEvent.ItemDto item : items) {
                agg.addItem(new SalesOrderLine(item.getSku(), item.getDescription(), item.getQuantity(), item.getPrice()));
                eventItems.add(item);
            }
        } else {
            agg.addItem(new SalesOrderLine("BED-KING", "Luxury King Size Bed", 1, 45000.00));
            eventItems.add(new SalesOrderCreatedEvent.ItemDto("BED-KING", "Luxury King Size Bed", 1, 45000.00));
        }

        SalesOrder saved = repository.save(agg);
        eventPublisher.publish(SalesOrderCreatedEvent.create(saved.getId(), saved.getReferenceCode(), saved.getTotalAmount(), eventItems));
        return saved;
    }

    @Transactional
    public SalesOrder createSalesOrder(String referenceCode) {
        return createSalesOrder(UUID.randomUUID(), referenceCode, 45000.00, null);
    }

    @Transactional(readOnly = true)
    public SalesOrder getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<SalesOrder> getAll() {
        return repository.findAll();
    }
}
