package com.furniture.erp.wms.infrastructure.repository;

import com.furniture.erp.wms.domain.entity.WarehouseBin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WarehouseBinRepository extends JpaRepository<WarehouseBin, UUID> {
}
