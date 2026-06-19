package com.furniture.erp.accounting.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.accounting.domain.entity.GeneralLedger;
import com.furniture.erp.accounting.domain.entity.JournalEntry;
import com.furniture.erp.accounting.domain.event.LedgerBalancedEvent;
import com.furniture.erp.accounting.infrastructure.repository.GeneralLedgerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class GeneralLedgerService {

    private final GeneralLedgerRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public GeneralLedgerService(GeneralLedgerRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public GeneralLedger createLedger(String referenceCode) {
        GeneralLedger agg = new GeneralLedger(referenceCode);
        agg.addItem(new JournalEntry("Initial item for " + referenceCode));
        GeneralLedger saved = repository.save(agg);
        
        eventPublisher.publish(LedgerBalancedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public GeneralLedger getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }
}
