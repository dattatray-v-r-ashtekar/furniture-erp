package com.furniture.erp.payroll.infrastructure.repository;

import com.furniture.erp.payroll.domain.entity.SalarySlip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SalarySlipRepository extends JpaRepository<SalarySlip, UUID> {
}
