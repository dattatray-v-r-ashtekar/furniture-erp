package com.furniture.erp.inventory.infrastructure.repository;

import com.furniture.erp.inventory.domain.entity.StockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface StockItemRepository extends JpaRepository<StockItem, UUID> {
    Optional<StockItem> findBySkuCode(String skuCode);
}
