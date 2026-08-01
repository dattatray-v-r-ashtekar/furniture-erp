package com.furniture.erp.hrms.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.hrms.domain.entity.EmployeeRecord;
import com.furniture.erp.hrms.domain.entity.AttendanceLog;
import com.furniture.erp.hrms.domain.event.ShiftAssignedEvent;
import com.furniture.erp.hrms.infrastructure.repository.EmployeeRecordRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class EmployeeRecordService {

    private final EmployeeRecordRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public EmployeeRecordService(EmployeeRecordRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public EmployeeRecord createEmployee(String referenceCode) {
        EmployeeRecord agg = new EmployeeRecord(referenceCode);
        agg.addItem(new AttendanceLog("Initial item for " + referenceCode));
        EmployeeRecord saved = repository.save(agg);
        
        eventPublisher.publish(ShiftAssignedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public EmployeeRecord getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<EmployeeRecord> getAll() {
        return repository.findAll();
    }
}
