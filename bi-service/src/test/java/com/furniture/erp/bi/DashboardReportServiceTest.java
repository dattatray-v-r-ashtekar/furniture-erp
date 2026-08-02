package com.furniture.erp.bi;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.bi.application.service.DashboardReportService;
import com.furniture.erp.bi.domain.entity.DashboardReport;
import com.furniture.erp.bi.infrastructure.repository.DashboardReportRepository;
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
class DashboardReportServiceTest {

    @Mock
    private DashboardReportRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private DashboardReportService service;

    @BeforeEach
    void setUp() {
        service = new DashboardReportService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createReport should generate BI dashboard report and publish event")
    void testCreateDashboardReport() {
        when(repository.save(any(DashboardReport.class))).thenAnswer(i -> i.getArgument(0));

        DashboardReport report = service.createReport("BI-EXEC-Q3");

        assertThat(report).isNotNull();
        assertThat(report.getReferenceCode()).isEqualTo("BI-EXEC-Q3");
        assertThat(report.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
