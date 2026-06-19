package com.furniture.erp.erpcentral.infrastructure.repository;

import com.furniture.erp.erpcentral.domain.entity.SalesOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalesOrderRepository extends JpaRepository<SalesOrder, UUID> {
}
