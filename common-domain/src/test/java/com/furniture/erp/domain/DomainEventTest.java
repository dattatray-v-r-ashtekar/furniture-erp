package com.furniture.erp.domain;

import com.furniture.erp.domain.entity.AggregateRoot;
import com.furniture.erp.domain.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

    static class SampleEvent implements DomainEvent<String> {
        private final String data;
        public SampleEvent(String data) {
            this.data = data;
        }
        public String getData() { return data; }
    }

    static class SampleAggregate extends AggregateRoot<UUID> {
        public SampleAggregate(UUID id) {
            super.setId(id);
        }
    }

    @Test
    @DisplayName("DomainEvent marker interface should encapsulate event payload")
    void testDomainEvent() {
        SampleEvent event = new SampleEvent("test-payload");
        assertThat(event).isNotNull();
        assertThat(event.getData()).isEqualTo("test-payload");
        assertThat(event).isInstanceOf(DomainEvent.class);
    }

    @Test
    @DisplayName("AggregateRoot and BaseEntity should maintain identity equality and hash code")
    void testAggregateRootIdentity() {
        UUID id = UUID.randomUUID();
        SampleAggregate agg1 = new SampleAggregate(id);
        SampleAggregate agg2 = new SampleAggregate(id);
        SampleAggregate agg3 = new SampleAggregate(UUID.randomUUID());

        assertThat(agg1.getId()).isEqualTo(id);
        assertThat(agg1).isEqualTo(agg2);
        assertThat(agg1).isNotEqualTo(agg3);
        assertThat(agg1.hashCode()).isEqualTo(agg2.hashCode());
    }
}
