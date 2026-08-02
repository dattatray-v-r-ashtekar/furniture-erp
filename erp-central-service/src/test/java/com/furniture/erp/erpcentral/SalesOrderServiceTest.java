package com.furniture.erp.erpcentral;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.erpcentral.application.service.SalesOrderService;
import com.furniture.erp.erpcentral.domain.entity.SalesOrder;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import com.furniture.erp.erpcentral.infrastructure.repository.SalesOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private SalesOrderService service;

    @BeforeEach
    void setUp() {
        service = new SalesOrderService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createSalesOrder should create single consolidated sales order with items and publish event")
    void testCreateSalesOrder() {
        when(repository.save(any(SalesOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<SalesOrderCreatedEvent.ItemDto> items = List.of(
                new SalesOrderCreatedEvent.ItemDto("TABLE-OAK", "Oak Table", 1, 75000.00),
                new SalesOrderCreatedEvent.ItemDto("CHAIR-01", "Ergo Chair", 2, 12500.00)
        );

        SalesOrder order = service.createSalesOrder(UUID.randomUUID(), "SO-12345", 100000.00, items);

        assertThat(order).isNotNull();
        assertThat(order.getReferenceCode()).isEqualTo("SO-12345");
        assertThat(order.getTotalAmount()).isEqualTo(100000.00);
        assertThat(order.getItems()).hasSize(2);
        verify(eventPublisher, times(1)).publish(any());
    }
}
