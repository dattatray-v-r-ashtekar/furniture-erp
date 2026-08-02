package com.furniture.erp.qms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.qms.application.service.QualityInspectionService;
import com.furniture.erp.qms.domain.entity.QualityInspection;
import com.furniture.erp.qms.infrastructure.repository.QualityInspectionRepository;
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
class QualityInspectionServiceTest {

    @Mock
    private QualityInspectionRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private QualityInspectionService service;

    @BeforeEach
    void setUp() {
        service = new QualityInspectionService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createInspection should record inspection entry and publish event")
    void testCreateInspection() {
        when(repository.save(any(QualityInspection.class))).thenAnswer(i -> i.getArgument(0));

        QualityInspection inspection = service.createInspection("QC-BATCH-2026");

        assertThat(inspection).isNotNull();
        assertThat(inspection.getReferenceCode()).isEqualTo("QC-BATCH-2026");
        assertThat(inspection.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
