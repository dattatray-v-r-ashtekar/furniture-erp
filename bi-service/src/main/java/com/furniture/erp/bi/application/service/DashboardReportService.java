package com.furniture.erp.bi.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.bi.domain.entity.DashboardReport;
import com.furniture.erp.bi.domain.entity.KpiMetric;
import com.furniture.erp.bi.domain.event.ReportGeneratedEvent;
import com.furniture.erp.bi.infrastructure.repository.DashboardReportRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DashboardReportService {

    private final DashboardReportRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public DashboardReportService(DashboardReportRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DashboardReport createReport(String referenceCode) {
        DashboardReport agg = new DashboardReport(referenceCode);
        agg.addItem(new KpiMetric("Initial item for " + referenceCode));
        DashboardReport saved = repository.save(agg);
        
        eventPublisher.publish(ReportGeneratedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public DashboardReport getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<DashboardReport> getAll() {
        return repository.findAll();
    }
}
