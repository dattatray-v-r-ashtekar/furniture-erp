package com.furniture.erp.dealerportal.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.dealerportal.domain.entity.WholesaleOrder;
import com.furniture.erp.dealerportal.domain.entity.WholesaleItem;
import com.furniture.erp.dealerportal.domain.event.BulkDiscountAppliedEvent;
import com.furniture.erp.dealerportal.infrastructure.repository.WholesaleOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WholesaleOrderService {

    private final WholesaleOrderRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public WholesaleOrderService(WholesaleOrderRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public WholesaleOrder createWholesaleOrder(String referenceCode) {
        WholesaleOrder agg = new WholesaleOrder(referenceCode);
        agg.addItem(new WholesaleItem("Initial item for " + referenceCode));
        WholesaleOrder saved = repository.save(agg);
        
        eventPublisher.publish(BulkDiscountAppliedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public WholesaleOrder getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }
}
