package com.furniture.erp.tms.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.tms.domain.entity.DeliveryRoute;
import com.furniture.erp.tms.domain.entity.DeliveryStop;
import com.furniture.erp.tms.domain.event.RouteStartedEvent;
import com.furniture.erp.tms.infrastructure.repository.DeliveryRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class DeliveryRouteService {

    private final DeliveryRouteRepository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public DeliveryRouteService(DeliveryRouteRepository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public DeliveryRoute createRoute(String referenceCode) {
        DeliveryRoute agg = new DeliveryRoute(referenceCode);
        agg.addItem(new DeliveryStop("Initial item for " + referenceCode));
        DeliveryRoute saved = repository.save(agg);
        
        eventPublisher.publish(RouteStartedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional(readOnly = true)
    public DeliveryRoute getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }
}
