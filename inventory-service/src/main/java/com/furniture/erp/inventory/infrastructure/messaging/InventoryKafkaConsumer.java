package com.furniture.erp.inventory.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.inventory.application.service.InventoryService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
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
    public void handleInventoryEventsFromKafka(Object message) {
        processInventoryEvents(message);
    }

    @EventListener
    public void handleLocalDomainEvent(DomainEvent<?> event) {
        String eventName = event.getClass().getSimpleName();
        if (eventName.contains("ProductionCompleted") || eventName.contains("GoodsReceived")) {
            processInventoryEvents(event);
        }
    }

    private void processInventoryEvents(Object message) {
        log.info("Inventory received event: {}", message);
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

            if (sku != null) {
                try {
                    inventoryService.createStockItem(sku, "Finished Furniture (" + sku + ")", "WH-BAY-FINISHED");
                } catch (Exception ignored) {}
                inventoryService.addStock(sku, qty);
                log.info("Inventory automatically incremented stock for SKU: {} by +{} units", sku, qty);
            }

        } catch (Exception e) {
            log.error("Failed to process inventory increment event", e);
        }
    }
}
