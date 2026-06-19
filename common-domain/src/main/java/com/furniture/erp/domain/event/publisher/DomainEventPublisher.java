package com.furniture.erp.domain.event.publisher;

import com.furniture.erp.domain.event.DomainEvent;

public interface DomainEventPublisher<T extends DomainEvent<?>> {
    void publish(T domainEvent);
}
