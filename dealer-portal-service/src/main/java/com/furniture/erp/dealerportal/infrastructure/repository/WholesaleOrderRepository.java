package com.furniture.erp.dealerportal.infrastructure.repository;

import com.furniture.erp.dealerportal.domain.entity.WholesaleOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface WholesaleOrderRepository extends JpaRepository<WholesaleOrder, UUID> {
}
