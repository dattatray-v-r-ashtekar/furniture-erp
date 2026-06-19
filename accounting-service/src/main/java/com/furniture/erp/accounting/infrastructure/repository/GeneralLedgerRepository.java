package com.furniture.erp.accounting.infrastructure.repository;

import com.furniture.erp.accounting.domain.entity.GeneralLedger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GeneralLedgerRepository extends JpaRepository<GeneralLedger, UUID> {
}
