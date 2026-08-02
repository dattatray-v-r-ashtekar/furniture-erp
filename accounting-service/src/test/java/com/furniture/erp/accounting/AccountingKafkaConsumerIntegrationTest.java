package com.furniture.erp.accounting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.accounting.domain.entity.GeneralLedger;
import com.furniture.erp.accounting.infrastructure.messaging.AccountingKafkaConsumer;
import com.furniture.erp.accounting.infrastructure.repository.GeneralLedgerRepository;
import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountingKafkaConsumerIntegrationTest {

    @Mock
    private GeneralLedgerRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Accounting consumer should deserialize multi-item B2CPaymentReceivedEvent and write ledger entry")
    void testHandlePaymentReceivedEvent() {
        when(repository.save(any(GeneralLedger.class))).thenAnswer(i -> i.getArgument(0));

        GeneralLedgerService ledgerService = new GeneralLedgerService(repository, eventPublisher);
        AccountingKafkaConsumer consumer = new AccountingKafkaConsumer(ledgerService, objectMapper);

        String kafkaMessageJson = "{" +
                "\"eventId\": \"a1b2c3d4-0000-0000-0000-000000000000\"," +
                "\"orderId\": \"b1b2c3d4-1111-2222-3333-444455556666\"," +
                "\"referenceCode\": \"ORD-B2C-9988\"," +
                "\"totalAmount\": 87500.50," +
                "\"items\": [" +
                "  {\"sku\": \"TABLE-OAK\", \"name\": \"Oak Dining Table\", \"quantity\": 1, \"price\": 75000.50}," +
                "  {\"sku\": \"CHAIR-OFFICE\", \"name\": \"Ergo Chair\", \"quantity\": 1, \"price\": 12500.00}" +
                "]" +
                "}";

        consumer.handlePaymentReceived(kafkaMessageJson);

        verify(repository, times(1)).save(argThat(ledger ->
                ledger.getAmount().equals(87500.50) &&
                ledger.getReferenceCode().equals("ORD-B2C-9988") &&
                ledger.getEntryType().equals("CREDIT") &&
                ledger.getItems().get(0).getDescription().contains("Oak Dining Table")
        ));
    }
}
