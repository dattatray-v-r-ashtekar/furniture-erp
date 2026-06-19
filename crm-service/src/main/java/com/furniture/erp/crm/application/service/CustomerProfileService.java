package com.furniture.erp.crm.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.crm.domain.entity.CustomerProfile;
import com.furniture.erp.crm.domain.entity.InteractionLog;
import com.furniture.erp.crm.domain.event.CustomerConvertedEvent;
import com.furniture.erp.crm.infrastructure.repository.CustomerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class CustomerProfileService {

    private final CustomerProfileRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public CustomerProfileService(CustomerProfileRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public CustomerProfile createCustomer(String referenceCode) {
        CustomerProfile agg = new CustomerProfile(referenceCode);
        agg.addItem(new InteractionLog("Initial item for " + referenceCode));
        CustomerProfile saved = repository.save(agg);
        
        eventPublisher.publish(CustomerConvertedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public CustomerProfile getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }
}
