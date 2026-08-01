package com.furniture.erp.ecommerce.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.ecommerce.domain.entity.OnlineOrder;
import com.furniture.erp.ecommerce.domain.entity.CartItem;
import com.furniture.erp.ecommerce.domain.event.PaymentProcessedEvent;
import com.furniture.erp.ecommerce.infrastructure.repository.OnlineOrderRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class OnlineOrderService {

    private final OnlineOrderRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public OnlineOrderService(OnlineOrderRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public OnlineOrder createOnlineOrder(String referenceCode) {
        OnlineOrder agg = new OnlineOrder(referenceCode);
        agg.addItem(new CartItem("Initial item for " + referenceCode));
        OnlineOrder saved = repository.save(agg);
        
        eventPublisher.publish(PaymentProcessedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public OnlineOrder getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<OnlineOrder> getAll() {
        return repository.findAll();
    }
}
