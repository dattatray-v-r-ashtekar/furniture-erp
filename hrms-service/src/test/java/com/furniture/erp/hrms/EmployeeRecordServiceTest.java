package com.furniture.erp.hrms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.hrms.application.service.EmployeeRecordService;
import com.furniture.erp.hrms.domain.entity.EmployeeRecord;
import com.furniture.erp.hrms.infrastructure.repository.EmployeeRecordRepository;
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
class EmployeeRecordServiceTest {

    @Mock
    private EmployeeRecordRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private EmployeeRecordService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeRecordService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createEmployee should save employee record and publish EmployeeOnboardedEvent")
    void testCreateEmployee() {
        when(repository.save(any(EmployeeRecord.class))).thenAnswer(i -> i.getArgument(0));

        EmployeeRecord emp = service.createEmployee("EMP-HR-001");
        assertThat(emp).isNotNull();
        assertThat(emp.getReferenceCode()).isEqualTo("EMP-HR-001");
        assertThat(emp.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
