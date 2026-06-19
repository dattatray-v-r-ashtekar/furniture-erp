package com.furniture.erp.hrms.infrastructure.repository;

import com.furniture.erp.hrms.domain.entity.EmployeeRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface EmployeeRecordRepository extends JpaRepository<EmployeeRecord, UUID> {
}
