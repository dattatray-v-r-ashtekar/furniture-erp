package com.furniture.erp.procurement.infrastructure.messaging;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("procurementKafkaDomainEventPublisher")
public class KafkaDomainEventPublisher implements DomainEventPublisher<DomainEvent<?>> {

    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void publish(DomainEvent<?> domainEvent) {
        String topic = domainEvent.getClass().getSimpleName();
        log.info("Publishing domain event: {} to topic: {}", domainEvent, topic);
        kafkaTemplate.send(topic, domainEvent);
    }
}

