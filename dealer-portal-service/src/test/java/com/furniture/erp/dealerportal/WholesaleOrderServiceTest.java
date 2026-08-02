package com.furniture.erp.dealerportal;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.dealerportal.application.service.WholesaleOrderService;
import com.furniture.erp.dealerportal.domain.entity.WholesaleOrder;
import com.furniture.erp.dealerportal.infrastructure.repository.WholesaleOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WholesaleOrderServiceTest {

    @Mock
    private WholesaleOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private WholesaleOrderService service;

    @BeforeEach
    void setUp() {
        service = new WholesaleOrderService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createWholesaleOrder should save and publish WholesaleOrderCreatedEvent")
    void testCreateWholesaleOrder() {
        when(repository.save(any(WholesaleOrder.class))).thenAnswer(i -> i.getArgument(0));

        WholesaleOrder order = service.createWholesaleOrder("WHOLESALE-DEALER-01");
        assertThat(order).isNotNull();
        assertThat(order.getReferenceCode()).isEqualTo("WHOLESALE-DEALER-01");
        assertThat(order.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
