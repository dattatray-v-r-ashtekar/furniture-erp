package com.furniture.erp.accounting.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.domain.event.DomainEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class AccountingKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(AccountingKafkaConsumer.class);
    private final GeneralLedgerService ledgerService;
    private final ObjectMapper objectMapper;

    public AccountingKafkaConsumer(GeneralLedgerService ledgerService, ObjectMapper objectMapper) {
        this.ledgerService = ledgerService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "B2CPaymentReceivedEvent", groupId = "accounting-service-group")
    public void handlePaymentReceived(Object message) {
        processPaymentReceived(message);
    }

    @EventListener
    public void handleLocalDomainEvent(DomainEvent<?> event) {
        String eventName = event.getClass().getSimpleName();
        if (eventName.contains("B2CPaymentReceived") || eventName.contains("PaymentReceived")) {
            processPaymentReceived(event);
        }
    }

    private void processPaymentReceived(Object message) {
        log.info("Accounting received B2CPaymentReceivedEvent: {}", message);
        try {
            Object val = message;
            if (val instanceof ConsumerRecord<?, ?> record) {
                val = record.value();
            }

            JsonNode root;
            if (val instanceof String str) {
                root = objectMapper.readTree(str);
            } else if (val instanceof byte[] bytes) {
                root = objectMapper.readTree(bytes);
            } else {
                root = objectMapper.valueToTree(val);
            }

            UUID orderId = null;
            if (root.has("orderId") && !root.get("orderId").isNull()) {
                orderId = UUID.fromString(root.get("orderId").asText());
            } else if (root.has("id") && !root.get("id").isNull()) {
                orderId = UUID.fromString(root.get("id").asText());
            } else if (root.has("aggregateId") && !root.get("aggregateId").isNull()) {
                orderId = UUID.fromString(root.get("aggregateId").asText());
            } else {
                orderId = UUID.randomUUID();
            }

            String ref = root.has("referenceCode") && !root.get("referenceCode").isNull()
                    ? root.get("referenceCode").asText()
                    : "B2C-PAY-" + System.currentTimeMillis();

            Double totalAmount = root.has("totalAmount") && !root.get("totalAmount").isNull()
                    ? root.get("totalAmount").asDouble()
                    : 45000.00;

            String desc = "B2C Payment for Order " + ref;
            if (root.has("items") && root.get("items").isArray() && root.get("items").size() > 0) {
                List<String> names = new ArrayList<>();
                for (JsonNode item : root.get("items")) {
                    if (item.has("name")) {
                        names.add(item.get("name").asText());
                    } else if (item.has("sku")) {
                        names.add(item.get("sku").asText());
                    }
                }
                desc += " (" + String.join(", ", names) + ")";
            }

            ledgerService.createLedger(orderId, ref, "REVENUE-B2C", "CREDIT", totalAmount, desc);
            log.info("Successfully recorded GL ledger entry for B2C order: {} (₹{})", ref, totalAmount);
        } catch (Exception e) {
            log.error("Failed to process payment event in Accounting", e);
        }
    }
}
