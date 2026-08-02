package com.furniture.erp.erpcentral.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.erpcentral.application.service.SalesOrderService;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ErpCentralKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(ErpCentralKafkaConsumer.class);
    private final SalesOrderService salesOrderService;
    private final ObjectMapper objectMapper;

    public ErpCentralKafkaConsumer(SalesOrderService salesOrderService, ObjectMapper objectMapper) {
        this.salesOrderService = salesOrderService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "B2CPaymentReceivedEvent", groupId = "erp-central-service-group")
    public void handlePaymentReceived(Object message) {
        log.info("ERP Central received B2CPaymentReceivedEvent from Kafka: {}", message);
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
            } else if (root.has("aggregateId") && !root.get("aggregateId").isNull()) {
                orderId = UUID.fromString(root.get("aggregateId").asText());
            } else {
                orderId = UUID.randomUUID();
            }

            String ref = root.has("referenceCode") && !root.get("referenceCode").isNull()
                    ? root.get("referenceCode").asText()
                    : "SO-B2C-" + System.currentTimeMillis();

            Double totalAmount = root.has("totalAmount") && !root.get("totalAmount").isNull()
                    ? root.get("totalAmount").asDouble()
                    : 45000.00;

            List<SalesOrderCreatedEvent.ItemDto> items = new ArrayList<>();
            if (root.has("items") && root.get("items").isArray()) {
                for (JsonNode itemNode : root.get("items")) {
                    String sku = itemNode.has("sku") ? itemNode.get("sku").asText() : "ITEM";
                    String name = itemNode.has("name") ? itemNode.get("name").asText() : (itemNode.has("description") ? itemNode.get("description").asText() : "Product");
                    int qty = itemNode.has("quantity") ? itemNode.get("quantity").asInt() : 1;
                    double price = itemNode.has("price") ? itemNode.get("price").asDouble() : 0.0;
                    items.add(new SalesOrderCreatedEvent.ItemDto(sku, name, qty, price));
                }
            }

            salesOrderService.createSalesOrder(orderId, ref, totalAmount, items);
            log.info("Successfully created ERP Sales Order {} with {} lines", ref, items.size());
        } catch (Exception e) {
            log.error("Failed to process payment event in ERP Central", e);
        }
    }
}
