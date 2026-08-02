package com.furniture.erp.tms.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.tms.domain.entity.DeliveryRoute;
import com.furniture.erp.tms.domain.entity.DeliveryStop;
import com.furniture.erp.tms.domain.event.RouteStartedEvent;
import com.furniture.erp.tms.infrastructure.repository.DeliveryRouteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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
        agg.addItem(new DeliveryStop("Delivery stop for " + referenceCode, "221B Baker St, London", referenceCode, "FX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase()));
        DeliveryRoute saved = repository.save(agg);
        
        eventPublisher.publish(RouteStartedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional
    public DeliveryRoute createRouteWithDetails(String referenceCode, String driverId, String deliveryAddress, String salesOrderId) {
        DeliveryRoute agg = new DeliveryRoute(referenceCode, driverId);
        String trackingNo = "FX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        agg.addItem(new DeliveryStop("Customer Delivery (" + referenceCode + ")", deliveryAddress, salesOrderId, trackingNo));
        DeliveryRoute saved = repository.save(agg);

        eventPublisher.publish(RouteStartedEvent.create(saved.getId()));
        return saved;
    }

    @Transactional
    public void startRoute(UUID routeId) {
        DeliveryRoute route = repository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));
        route.startRoute();
        repository.save(route);
    }

    @Transactional
    public void completeStop(UUID routeId, UUID stopId) {
        DeliveryRoute route = repository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));
        route.completeStop(stopId);
        repository.save(route);
    }

    @Transactional
    public void completeRoute(UUID routeId) {
        DeliveryRoute route = repository.findById(routeId)
                .orElseThrow(() -> new IllegalArgumentException("Route not found: " + routeId));
        route.completeRoute();
        repository.save(route);
    }

    @Transactional(readOnly = true)
    public DeliveryRoute getById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }

    public List<DeliveryRoute> getAll() {
        return repository.findAll();
    }
}
