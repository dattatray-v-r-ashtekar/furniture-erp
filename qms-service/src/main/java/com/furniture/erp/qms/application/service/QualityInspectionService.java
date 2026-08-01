package com.furniture.erp.qms.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.qms.domain.entity.QualityInspection;
import com.furniture.erp.qms.domain.entity.DefectLog;
import com.furniture.erp.qms.domain.event.InspectionFailedEvent;
import com.furniture.erp.qms.infrastructure.repository.QualityInspectionRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class QualityInspectionService {

    private final QualityInspectionRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public QualityInspectionService(QualityInspectionRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public QualityInspection createInspection(String referenceCode) {
        QualityInspection agg = new QualityInspection(referenceCode);
        agg.addItem(new DefectLog("Initial item for " + referenceCode));
        QualityInspection saved = repository.save(agg);
        
        eventPublisher.publish(InspectionFailedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public QualityInspection getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<QualityInspection> getAll() {
        return repository.findAll();
    }
}
