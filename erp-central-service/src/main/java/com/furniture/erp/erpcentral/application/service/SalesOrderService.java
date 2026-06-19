package com.furniture.erp.erpcentral.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.erpcentral.domain.entity.SalesOrder;
import com.furniture.erp.erpcentral.domain.entity.SalesOrderLine;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import com.furniture.erp.erpcentral.infrastructure.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public SalesOrder createSalesOrder(String referenceCode) {
        SalesOrder agg = new SalesOrder(referenceCode);
        agg.addItem(new SalesOrderLine("Initial item for " + referenceCode));
        SalesOrder saved = repository.save(agg);
        
        eventPublisher.publish(SalesOrderCreatedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public SalesOrder getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }
}
