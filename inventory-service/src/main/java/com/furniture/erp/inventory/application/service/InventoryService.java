package com.furniture.erp.inventory.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.inventory.domain.entity.StockItem;
import com.furniture.erp.inventory.domain.event.StockUpdatedEvent;
import com.furniture.erp.inventory.infrastructure.repository.StockItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryService {

    private final StockItemRepository stockItemRepository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public InventoryService(StockItemRepository stockItemRepository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {
        this.stockItemRepository = stockItemRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public StockItem createStockItem(String skuCode, String description, String locationBin) {
        if (stockItemRepository.findBySkuCode(skuCode).isPresent()) {
            throw new IllegalArgumentException("SKU Code already exists: " + skuCode);
        }
        StockItem newStockItem = new StockItem(skuCode, description, locationBin);
        return stockItemRepository.save(newStockItem);
    }

    @Transactional
    public void addStock(String skuCode, int quantity) {
        StockItem stockItem = stockItemRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new IllegalArgumentException("Stock Item not found: " + skuCode));
        
        stockItem.addStock(quantity);
        stockItemRepository.save(stockItem);

        eventPublisher.publish(StockUpdatedEvent.create(stockItem.getId(), stockItem.getSkuCode(), stockItem.getAvailableQuantity()));
    }

    @Transactional
    public void deductStock(String skuCode, int quantity) {
        StockItem stockItem = stockItemRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new IllegalArgumentException("Stock Item not found: " + skuCode));
        
        stockItem.deductStock(quantity);
        stockItemRepository.save(stockItem);

        eventPublisher.publish(StockUpdatedEvent.create(stockItem.getId(), stockItem.getSkuCode(), stockItem.getAvailableQuantity()));
    }

    @Transactional(readOnly = true)
    public StockItem getStock(String skuCode) {
        return stockItemRepository.findBySkuCode(skuCode)
                .orElseThrow(() -> new IllegalArgumentException("Stock Item not found: " + skuCode));
    }
}
