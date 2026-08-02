package com.furniture.erp.payroll;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.payroll.application.service.SalarySlipService;
import com.furniture.erp.payroll.domain.entity.SalarySlip;
import com.furniture.erp.payroll.infrastructure.repository.SalarySlipRepository;
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
class SalarySlipServiceTest {

    @Mock
    private SalarySlipRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private SalarySlipService service;

    @BeforeEach
    void setUp() {
        service = new SalarySlipService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createSalarySlip should save salary slip and publish SalaryDisbursedEvent")
    void testCreateSalarySlip() {
        when(repository.save(any(SalarySlip.class))).thenAnswer(i -> i.getArgument(0));

        SalarySlip slip = service.createSalarySlip("SLIP-2026-08");
        assertThat(slip).isNotNull();
        assertThat(slip.getReferenceCode()).isEqualTo("SLIP-2026-08");
        assertThat(slip.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
