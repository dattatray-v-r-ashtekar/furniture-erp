package com.furniture.erp.payroll.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.payroll.domain.entity.SalarySlip;
import com.furniture.erp.payroll.domain.entity.TaxDeduction;
import com.furniture.erp.payroll.domain.event.SalaryDisbursedEvent;
import com.furniture.erp.payroll.infrastructure.repository.SalarySlipRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class SalarySlipService {

    private final SalarySlipRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public SalarySlipService(SalarySlipRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public SalarySlip createSalarySlip(String referenceCode) {
        SalarySlip agg = new SalarySlip(referenceCode);
        agg.addItem(new TaxDeduction("Initial item for " + referenceCode));
        SalarySlip saved = repository.save(agg);
        
        eventPublisher.publish(SalaryDisbursedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public SalarySlip getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }
}
