package com.furniture.erp.payroll.infrastructure.rest;

import com.furniture.erp.payroll.application.service.SalarySlipService;
import com.furniture.erp.payroll.domain.entity.SalarySlip;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payroll/slips")
public class SalarySlipController {

    private final SalarySlipService service;

    public SalarySlipController(SalarySlipService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SalarySlip> create(@RequestBody CreateRequest request) {
        SalarySlip agg = service.createSalarySlip(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalarySlip> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}

record CreateRequest(String referenceCode) {}
