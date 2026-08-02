package com.furniture.erp.inventory.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.inventory.application.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(InventoryKafkaConsumer.class);
    private final InventoryService inventoryService;
    private final ObjectMapper objectMapper;

    public InventoryKafkaConsumer(InventoryService inventoryService, ObjectMapper objectMapper) {
        this.inventoryService = inventoryService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"ProductionCompletedEvent", "GoodsReceivedEvent"}, groupId = "inventory-service-group")
    public void handleInventoryEvents(Object message) {
        log.info("Inventory received event from Kafka: {}", message);
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

            String sku = null;
            int qty = 1;

            if (root.has("productSku")) {
                sku = root.get("productSku").asText();
            } else if (root.has("sku")) {
                sku = root.get("sku").asText();
            } else if (root.has("skuCode")) {
                sku = root.get("skuCode").asText();
            }

            if (root.has("goodQuantity") && root.get("goodQuantity").asInt() > 0) {
                qty = root.get("goodQuantity").asInt();
            } else if (root.has("totalGoodQuantity") && root.get("totalGoodQuantity").asInt() > 0) {
                qty = root.get("totalGoodQuantity").asInt();
            } else if (root.has("quantity") && root.get("quantity").asInt() > 0) {
                qty = root.get("quantity").asInt();
            } else if (root.has("targetQuantity") && root.get("targetQuantity").asInt() > 0) {
                qty = root.get("targetQuantity").asInt();
            }

            if (qty <= 0) {
                qty = 1;
            }

            if (sku != null && !sku.isBlank()) {
                try {
                    inventoryService.addStock(sku, qty);
                    log.info("Inventory successfully incremented stock for SKU: {} by Qty: {}", sku, qty);
                } catch (IllegalArgumentException notFoundEx) {
                    // Stock item doesn't exist yet, create it and add stock
                    inventoryService.createStockItem(sku, "Finished Good (" + sku + ")", "BIN-FG-01");
                    inventoryService.addStock(sku, qty);
                    log.info("Inventory created new StockItem and incremented stock for SKU: {} by Qty: {}", sku, qty);
                }
            }
        } catch (Exception e) {
            log.error("Failed to process inventory event in InventoryKafkaConsumer", e);
        }
    }
}
