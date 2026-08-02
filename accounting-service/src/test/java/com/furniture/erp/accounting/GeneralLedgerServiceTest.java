package com.furniture.erp.accounting;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.accounting.domain.entity.GeneralLedger;
import com.furniture.erp.accounting.infrastructure.repository.GeneralLedgerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralLedgerServiceTest {

    @Mock
    private GeneralLedgerRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private GeneralLedgerService service;

    @BeforeEach
    void setUp() {
        service = new GeneralLedgerService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createLedger should create credit ledger entry and publish LedgerBalancedEvent")
    void testCreateLedger() {
        when(repository.save(any(GeneralLedger.class))).thenAnswer(i -> i.getArgument(0));

        UUID orderId = UUID.randomUUID();
        GeneralLedger ledger = service.createLedger(orderId, "ORD-999", "REV-B2C", "CREDIT", 87500.50, "B2C Revenue");

        assertThat(ledger).isNotNull();
        assertThat(ledger.getAmount()).isEqualTo(87500.50);
        assertThat(ledger.getEntryType()).isEqualTo("CREDIT");
        assertThat(ledger.getReferenceCode()).isEqualTo("ORD-999");
        assertThat(ledger.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
