package com.furniture.erp.ecommerce;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.ecommerce.application.service.OnlineOrderService;
import com.furniture.erp.ecommerce.domain.entity.OnlineOrder;
import com.furniture.erp.ecommerce.infrastructure.repository.OnlineOrderRepository;
import com.furniture.erp.ecommerce.infrastructure.rest.OnlineOrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnlineOrderServiceTest {

    @Mock
    private OnlineOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private OnlineOrderService service;

    @BeforeEach
    void setUp() {
        service = new OnlineOrderService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createOnlineOrder should aggregate multi-item cart correctly and publish event")
    void testCreateOnlineOrderWithMultipleItems() {
        when(repository.save(any(OnlineOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<OnlineOrderController.ItemRequest> items = List.of(
                new OnlineOrderController.ItemRequest("TABLE-OAK", "Custom Oak Table", 1, 75000.50),
                new OnlineOrderController.ItemRequest("CHAIR-OFFICE", "Ergo Chair", 2, 12500.00)
        );

        OnlineOrder order = service.createOnlineOrder("ORD-101", 100000.50, items);

        assertThat(order).isNotNull();
        assertThat(order.getReferenceCode()).isEqualTo("ORD-101");
        assertThat(order.getTotalAmount()).isEqualTo(100000.50);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getStatus()).isEqualTo("PAID");

        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    @DisplayName("createOnlineOrder should parse Map items correctly when json map is passed")
    void testCreateOnlineOrderWithMapItems() {
        when(repository.save(any(OnlineOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<Map<String, Object>> mapItems = List.of(
                Map.of("sku", "BED-KING", "name", "King Bed", "quantity", 1, "price", 45000.00)
        );

        OnlineOrder order = service.createOnlineOrder("ORD-102", 0.0, mapItems);

        assertThat(order).isNotNull();
        assertThat(order.getTotalAmount()).isEqualTo(45000.00);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getSku()).isEqualTo("BED-KING");
    }

    @Test
    @DisplayName("createOnlineOrder should fallback to default bed if items list is empty")
    void testCreateOnlineOrderFallback() {
        when(repository.save(any(OnlineOrder.class))).thenAnswer(i -> i.getArgument(0));

        OnlineOrder order = service.createOnlineOrder("ORD-EMPTY", 0.0, List.of());

        assertThat(order).isNotNull();
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getSku()).isEqualTo("BED-KING");
        assertThat(order.getTotalAmount()).isEqualTo(45000.00);
    }
}
