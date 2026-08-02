package com.furniture.erp.tms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.tms.application.service.DeliveryRouteService;
import com.furniture.erp.tms.domain.entity.DeliveryRoute;
import com.furniture.erp.tms.domain.entity.DeliveryStop;
import com.furniture.erp.tms.infrastructure.repository.DeliveryRouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryRouteServiceTest {

    @Mock
    private DeliveryRouteRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private DeliveryRouteService service;

    @BeforeEach
    void setUp() {
        service = new DeliveryRouteService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createRoute should initialize delivery route and publish RouteStartedEvent")
    void testDeliveryRouteCreation() {
        when(repository.save(any(DeliveryRoute.class))).thenAnswer(i -> i.getArgument(0));

        DeliveryRoute route = service.createRoute("ROUTE-BLR-01");
        assertThat(route).isNotNull();
        assertThat(route.getReferenceCode()).isEqualTo("ROUTE-BLR-01");
        assertThat(route.getItems()).hasSize(1);
        assertThat(route.getStatus()).isEqualTo("SCHEDULED");
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    @DisplayName("startRoute and completeStop should update status to IN_TRANSIT and COMPLETED")
    void testStartAndCompleteRoute() {
        DeliveryRoute route = new DeliveryRoute("ROUTE-EXP-01", "DRV-102");
        DeliveryStop stop = new DeliveryStop("Customer Stop", "123 Main St", "SO-1", "FX-999");
        route.addItem(stop);

        when(repository.findById(route.getId())).thenReturn(Optional.of(route));
        when(repository.save(any(DeliveryRoute.class))).thenAnswer(i -> i.getArgument(0));

        service.startRoute(route.getId());
        assertThat(route.getStatus()).isEqualTo("IN_TRANSIT");
        assertThat(stop.getStatus()).isEqualTo("IN_TRANSIT");

        service.completeStop(route.getId(), stop.getId());
        assertThat(stop.getStatus()).isEqualTo("COMPLETED");
        assertThat(route.getStatus()).isEqualTo("COMPLETED");
    }
}
