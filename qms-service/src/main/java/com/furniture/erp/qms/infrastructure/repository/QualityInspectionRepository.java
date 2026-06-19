package com.furniture.erp.qms.infrastructure.repository;

import com.furniture.erp.qms.domain.entity.QualityInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface QualityInspectionRepository extends JpaRepository<QualityInspection, UUID> {
}
