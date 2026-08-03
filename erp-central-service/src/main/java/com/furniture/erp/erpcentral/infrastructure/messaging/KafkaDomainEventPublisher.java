package com.furniture.erp.erpcentral.infrastructure.messaging;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component("erpcentralKafkaDomainEventPublisher")
public class KafkaDomainEventPublisher implements DomainEventPublisher<DomainEvent<?>> {

    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ApplicationEventPublisher applicationEventPublisher;

    public KafkaDomainEventPublisher(
            @Autowired(required = false) KafkaTemplate<String, Object> kafkaTemplate,
            ApplicationEventPublisher applicationEventPublisher) {
        this.kafkaTemplate = kafkaTemplate;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent<?> domainEvent) {
        String topic = domainEvent.getClass().getSimpleName();
        log.info("Publishing domain event: {} to topic: {}", domainEvent, topic);

        if (applicationEventPublisher != null) {
            try {
                applicationEventPublisher.publishEvent(domainEvent);
            } catch (Exception e) {
                log.warn("In-process ApplicationEvent dispatch warning: {}", e.getMessage());
            }
        }

        if (kafkaTemplate != null) {
            try {
                kafkaTemplate.send(topic, domainEvent);
            } catch (Exception e) {
                log.warn("Kafka dispatch warning for topic {}: {}", topic, e.getMessage());
            }
        }
    }
}
