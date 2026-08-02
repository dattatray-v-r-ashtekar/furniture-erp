package com.furniture.erp.procurement;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.procurement.application.service.ProcurementService;
import com.furniture.erp.procurement.domain.entity.PurchaseOrder;
import com.furniture.erp.procurement.infrastructure.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest {

    @Mock
    private PurchaseOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private ProcurementService service;

    @BeforeEach
    void setUp() {
        service = new ProcurementService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createDraftOrder and issueOrder lifecycle")
    void testPurchaseOrderLifecycle() {
        when(repository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder order = service.createDraftOrder("VENDOR-001");
        assertThat(order).isNotNull();
        assertThat(order.getVendorId()).isEqualTo("VENDOR-001");

        UUID orderId = order.getId();
        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        service.addLineItem(orderId, "WOOD-01", 50, BigDecimal.valueOf(120.00));
        assertThat(order.getLines()).hasSize(1);

        service.issueOrder(orderId);
        verify(eventPublisher, times(1)).publish(any());
    }
}
