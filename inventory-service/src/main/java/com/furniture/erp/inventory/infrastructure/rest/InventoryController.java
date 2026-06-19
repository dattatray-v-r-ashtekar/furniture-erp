package com.furniture.erp.inventory.infrastructure.rest;

import com.furniture.erp.inventory.application.service.InventoryService;
import com.furniture.erp.inventory.domain.entity.StockItem;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/items")
    public ResponseEntity<StockItem> createStockItem(@RequestBody CreateStockItemRequest request) {
        StockItem stockItem = inventoryService.createStockItem(
                request.skuCode(), request.description(), request.locationBin()
        );
        return ResponseEntity.ok(stockItem);
    }

    @PostMapping("/items/{skuCode}/add")
    public ResponseEntity<Void> addStock(@PathVariable String skuCode, @RequestBody UpdateStockRequest request) {
        inventoryService.addStock(skuCode, request.quantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/items/{skuCode}/deduct")
    public ResponseEntity<Void> deductStock(@PathVariable String skuCode, @RequestBody UpdateStockRequest request) {
        inventoryService.deductStock(skuCode, request.quantity());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/items/{skuCode}")
    public ResponseEntity<StockItem> getStock(@PathVariable String skuCode) {
        return ResponseEntity.ok(inventoryService.getStock(skuCode));
    }
}

record CreateStockItemRequest(String skuCode, String description, String locationBin) {}
record UpdateStockRequest(Integer quantity) {}
