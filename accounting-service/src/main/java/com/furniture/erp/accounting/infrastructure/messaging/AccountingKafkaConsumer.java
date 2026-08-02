package com.furniture.erp.accounting.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

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
    public void handlePaymentReceived(String message) {
        log.info("Accounting received B2CPaymentReceivedEvent from Kafka: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);
            
            UUID orderId = null;
            if (root.has("orderId") && !root.get("orderId").isNull()) {
                orderId = UUID.fromString(root.get("orderId").asText());
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

            StringBuilder desc = new StringBuilder("B2C Order Revenue " + ref);
            if (root.has("items") && root.get("items").isArray() && root.get("items").size() > 0) {
                desc.append(" (");
                boolean first = true;
                for (JsonNode item : root.get("items")) {
                    if (!first) desc.append(", ");
                    String name = item.has("name") ? item.get("name").asText() : (item.has("sku") ? item.get("sku").asText() : "Item");
                    int qty = item.has("quantity") ? item.get("quantity").asInt() : 1;
                    desc.append(name).append(" x").append(qty);
                    first = false;
                }
                desc.append(")");
            }

            ledgerService.createLedger(orderId, ref, "REVENUE-B2C", "CREDIT", totalAmount, desc.toString());
            log.info("Successfully recorded General Ledger entry for order {} amount ₹{}", ref, totalAmount);
        } catch (Exception e) {
            log.error("Failed to process payment event in Accounting", e);
        }
    }
}
