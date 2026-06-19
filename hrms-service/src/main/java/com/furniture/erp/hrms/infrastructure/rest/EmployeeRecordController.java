package com.furniture.erp.hrms.infrastructure.rest;

import com.furniture.erp.hrms.application.service.EmployeeRecordService;
import com.furniture.erp.hrms.domain.entity.EmployeeRecord;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hrms/employees")
public class EmployeeRecordController {

    private final EmployeeRecordService service;

    public EmployeeRecordController(EmployeeRecordService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EmployeeRecord> create(@RequestBody CreateRequest request) {
        EmployeeRecord agg = service.createEmployee(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeRecord> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}

record CreateRequest(String referenceCode) {}
