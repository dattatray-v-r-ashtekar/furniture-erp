package com.furniture.erp.mes;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.mes.application.service.MesService;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import com.furniture.erp.mes.domain.entity.ProductionStatus;
import com.furniture.erp.mes.domain.entity.WorkOrder;
import com.furniture.erp.mes.domain.entity.WorkOrderStatus;
import com.furniture.erp.mes.infrastructure.repository.ProductionOrderRepository;
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
class MesServiceTest {

    @Mock
    private ProductionOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private MesService service;

    @BeforeEach
    void setUp() {
        service = new MesService(repository, eventPublisher);
    }

    @Test
    @DisplayName("planProduction should initialize work orders with 3 standard routing steps")
    void testPlanProduction() {
        when(repository.save(any(ProductionOrder.class))).thenAnswer(i -> i.getArgument(0));

        ProductionOrder order = service.planProduction("BED-KING", 2);

        assertThat(order).isNotNull();
        assertThat(order.getProductSku()).isEqualTo("BED-KING");
        assertThat(order.getTargetQuantity()).isEqualTo(2);
        assertThat(order.getWorkOrders()).hasSize(3);
        assertThat(order.getStatus()).isEqualTo(ProductionStatus.PLANNED);
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    @DisplayName("completeProductionOrder should auto-complete all pending work orders and publish ProductionCompletedEvent")
    void testCompleteProductionOrder() {
        ProductionOrder order = new ProductionOrder("TABLE-OAK", 1);
        order.addWorkOrder(new WorkOrder("Cutting", "Saw-01"));
        order.addWorkOrder(new WorkOrder("Assembly", "Asm-01"));
        order.startProduction();

        UUID orderId = order.getId();
        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(repository.save(any(ProductionOrder.class))).thenAnswer(i -> i.getArgument(0));

        service.completeProductionOrder(orderId);

        assertThat(order.getStatus()).isEqualTo(ProductionStatus.COMPLETED);
        assertThat(order.getWorkOrders()).allMatch(wo -> wo.getStatus() == WorkOrderStatus.DONE);
        verify(eventPublisher, times(1)).publish(any());
    }
}
