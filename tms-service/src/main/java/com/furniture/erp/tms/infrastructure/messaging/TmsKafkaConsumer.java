package com.furniture.erp.tms.infrastructure.messaging;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.tms.application.service.DeliveryRouteService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TmsKafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(TmsKafkaConsumer.class);
    private final DeliveryRouteService routeService;
    private final ObjectMapper objectMapper;

    public TmsKafkaConsumer(DeliveryRouteService routeService, ObjectMapper objectMapper) {
        this.routeService = routeService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = {"SalesOrderCreatedEvent", "ProductionCompletedEvent"}, groupId = "tms-service-group")
    public void handleOrderEvents(Object message) {
        log.info("TMS received event from Kafka: {}", message);
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

            String refCode = "ROUTE-" + System.currentTimeMillis();
            String salesOrderId = "SO-GEN";
            String destination = "742 Evergreen Terrace, Springfield";

            if (root.has("referenceCode")) {
                refCode = root.get("referenceCode").asText();
            } else if (root.has("orderId")) {
                refCode = "ROUTE-" + root.get("orderId").asText().substring(0, 8);
                salesOrderId = root.get("orderId").asText();
            } else if (root.has("productionOrderId")) {
                refCode = "ROUTE-MES-" + root.get("productionOrderId").asText().substring(0, 8);
            }

            routeService.createRouteWithDetails(refCode, "DRV-102 (Express Fleet)", destination, salesOrderId);
            log.info("TMS successfully auto-scheduled delivery route for reference: {}", refCode);

        } catch (Exception e) {
            log.error("Failed to process delivery route event in TMS", e);
        }
    }
}
