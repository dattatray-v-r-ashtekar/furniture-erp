package com.furniture.erp.wms.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.wms.domain.entity.WarehouseBin;
import com.furniture.erp.wms.domain.entity.BinMovement;
import com.furniture.erp.wms.domain.event.BinCapacityReachedEvent;
import com.furniture.erp.wms.infrastructure.repository.WarehouseBinRepository;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class WarehouseBinService {

    private final WarehouseBinRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public WarehouseBinService(WarehouseBinRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public WarehouseBin createBin(String referenceCode) {
        WarehouseBin agg = new WarehouseBin(referenceCode);
        agg.addItem(new BinMovement("Initial item for " + referenceCode));
        WarehouseBin saved = repository.save(agg);
        
        eventPublisher.publish(BinCapacityReachedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public WarehouseBin getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<WarehouseBin> getAll() {
        return repository.findAll();
    }
}
