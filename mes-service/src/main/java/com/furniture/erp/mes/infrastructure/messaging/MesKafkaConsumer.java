package com.furniture.erp.mes.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.mes.application.service.MesService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
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
    public void handleSalesOrderCreated(Object message) {
        log.info("MES received SalesOrderCreatedEvent from Kafka: {}", message);
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

            String salesOrderId = root.has("orderId") ? root.get("orderId").asText() : (root.has("id") ? root.get("id").asText() : null);
            String refCode = root.has("referenceCode") ? root.get("referenceCode").asText() : (root.has("externalReference") ? root.get("externalReference").asText() : null);

            boolean plannedAny = false;

            if (root.has("items") && root.get("items").isArray() && root.get("items").size() > 0) {
                for (JsonNode item : root.get("items")) {
                    String sku = item.has("sku") ? item.get("sku").asText() : "BED-KING";
                    int qty = item.has("quantity") ? item.get("quantity").asInt() : 1;
                    mesService.planProduction(sku, qty, salesOrderId, refCode);
                    log.info("Successfully planned MES Production Order for SKU: {} Qty: {} [SalesOrderID: {}, Ref: {}]", sku, qty, salesOrderId, refCode);
                    plannedAny = true;
                }
            }

            if (!plannedAny) {
                mesService.planProduction("BED-KING", 1, salesOrderId, refCode);
                log.info("Successfully planned default Production Order in MES [SalesOrderID: {}, Ref: {}]", salesOrderId, refCode);
            }
        } catch (Exception e) {
            log.error("Failed to plan production order in MES", e);
        }
    }
}
