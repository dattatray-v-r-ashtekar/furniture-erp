package com.furniture.erp.ecommerce.infrastructure.repository;

import com.furniture.erp.ecommerce.domain.entity.OnlineOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OnlineOrderRepository extends JpaRepository<OnlineOrder, UUID> {
}
