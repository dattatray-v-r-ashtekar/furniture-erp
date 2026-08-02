package com.furniture.erp.crm;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.crm.application.service.CustomerProfileService;
import com.furniture.erp.crm.domain.entity.CustomerProfile;
import com.furniture.erp.crm.infrastructure.repository.CustomerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {

    @Mock
    private CustomerProfileRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private CustomerProfileService service;

    @BeforeEach
    void setUp() {
        service = new CustomerProfileService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createCustomer should initialize customer profile and publish event")
    void testCustomerCreation() {
        when(repository.save(any(CustomerProfile.class))).thenAnswer(i -> i.getArgument(0));

        CustomerProfile customer = service.createCustomer("CUST-99001");
        assertThat(customer).isNotNull();
        assertThat(customer.getReferenceCode()).isEqualTo("CUST-99001");
        assertThat(customer.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
