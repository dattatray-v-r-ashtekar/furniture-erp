package com.furniture.erp.mes.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.mes.application.service.MesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class MesKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(MesKafkaConsumer.class);
    private final MesService mesService;
    private final ObjectMapper objectMapper;

    public MesKafkaConsumer(MesService mesService, ObjectMapper objectMapper) {
        this.mesService = mesService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "SalesOrderCreatedEvent", groupId = "mes-service-group")
    public void handleSalesOrderCreated(String message) {
        log.info("MES received SalesOrderCreatedEvent from Kafka: {}", message);
        try {
            JsonNode root = objectMapper.readTree(message);
            boolean plannedAny = false;

            if (root.has("items") && root.get("items").isArray() && root.get("items").size() > 0) {
                for (JsonNode item : root.get("items")) {
                    String sku = item.has("sku") ? item.get("sku").asText() : "BED-KING";
                    int qty = item.has("quantity") ? item.get("quantity").asInt() : 1;
                    mesService.planProduction(sku, qty);
                    log.info("Successfully planned MES Production Order for SKU: {} Qty: {}", sku, qty);
                    plannedAny = true;
                }
            }

            if (!plannedAny) {
                mesService.planProduction("BED-KING", 1);
                log.info("Successfully planned default Production Order in MES");
            }
        } catch (Exception e) {
            log.error("Failed to plan production order in MES", e);
        }
    }
}
