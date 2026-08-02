package com.furniture.erp.wms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.wms.application.service.WarehouseBinService;
import com.furniture.erp.wms.domain.entity.WarehouseBin;
import com.furniture.erp.wms.infrastructure.repository.WarehouseBinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseBinServiceTest {

    @Mock
    private WarehouseBinRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private WarehouseBinService service;

    @BeforeEach
    void setUp() {
        service = new WarehouseBinService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createBin should initialize warehouse bin and publish event")
    void testWarehouseBinCreation() {
        when(repository.save(any(WarehouseBin.class))).thenAnswer(i -> i.getArgument(0));

        WarehouseBin bin = service.createBin("BIN-RACK-01");
        assertThat(bin).isNotNull();
        assertThat(bin.getReferenceCode()).isEqualTo("BIN-RACK-01");
        assertThat(bin.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
