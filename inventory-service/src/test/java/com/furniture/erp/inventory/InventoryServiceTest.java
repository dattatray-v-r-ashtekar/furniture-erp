package com.furniture.erp.inventory;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.inventory.application.service.InventoryService;
import com.furniture.erp.inventory.domain.entity.StockItem;
import com.furniture.erp.inventory.infrastructure.repository.StockItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StockItemRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createStockItem should save and return stock item")
    void testCreateStockItem() {
        when(repository.findBySkuCode("WOOD-OAK")).thenReturn(Optional.empty());
        when(repository.save(any(StockItem.class))).thenAnswer(i -> i.getArgument(0));

        StockItem item = service.createStockItem("WOOD-OAK", "Solid Oak Planks", "BIN-A1");

        assertThat(item).isNotNull();
        assertThat(item.getSkuCode()).isEqualTo("WOOD-OAK");
        assertThat(item.getLocationBin()).isEqualTo("BIN-A1");
    }

    @Test
    @DisplayName("addStock and deductStock should modify quantities and publish events")
    void testAddAndDeductStock() {
        StockItem item = new StockItem("STEEL-ROD", "Steel Rods", "BIN-B2");
        when(repository.findBySkuCode("STEEL-ROD")).thenReturn(Optional.of(item));
        when(repository.save(any(StockItem.class))).thenAnswer(i -> i.getArgument(0));

        service.addStock("STEEL-ROD", 100);
        assertThat(item.getAvailableQuantity()).isEqualTo(100);

        service.deductStock("STEEL-ROD", 30);
        assertThat(item.getAvailableQuantity()).isEqualTo(70);

        verify(eventPublisher, times(2)).publish(any());
    }
}
