package com.furniture.erp.inventory.infrastructure.config;

import com.furniture.erp.inventory.application.service.InventoryService;
import com.furniture.erp.inventory.infrastructure.repository.StockItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class InventoryDataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(InventoryDataInitializer.class);

    private final InventoryService inventoryService;
    private final StockItemRepository repository;

    public InventoryDataInitializer(InventoryService inventoryService, StockItemRepository repository) {
        this.inventoryService = inventoryService;
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        seedIfMissing("BED-KING", "Luxury King Size Bed (Finished Good)", "BIN-FG-A1", 5);
        seedIfMissing("TABLE-OAK", "Luxury Oak Dining Table (Finished Good)", "BIN-FG-B2", 3);
        seedIfMissing("CHAIR-OFFICE", "Ergo Executive Chair (Finished Good)", "BIN-FG-C3", 10);
        seedIfMissing("WOOD-01", "Solid Teak & Oak Lumber Planks", "BIN-RAW-01", 50);
        seedIfMissing("STEEL-01", "Cold-Rolled Steel Tubes & Fasteners", "BIN-RAW-02", 100);
    }

    private void seedIfMissing(String sku, String description, String bin, int initialQty) {
        if (repository.findBySkuCode(sku).isEmpty()) {
            try {
                inventoryService.createStockItem(sku, description, bin);
                inventoryService.addStock(sku, initialQty);
                log.info("Initialized default stock item: {} with quantity: {}", sku, initialQty);
            } catch (Exception e) {
                log.warn("Could not seed default inventory item {}: {}", sku, e.getMessage());
            }
        }
    }
}
