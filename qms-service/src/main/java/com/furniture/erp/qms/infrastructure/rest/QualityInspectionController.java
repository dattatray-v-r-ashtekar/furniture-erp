package com.furniture.erp.qms.infrastructure.rest;

import com.furniture.erp.qms.application.service.QualityInspectionService;
import com.furniture.erp.qms.domain.entity.QualityInspection;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/qms/inspections")
public class QualityInspectionController {

    private final QualityInspectionService service;

    public QualityInspectionController(QualityInspectionService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<QualityInspection> create(@RequestBody CreateRequest request) {
        QualityInspection agg = service.createInspection(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QualityInspection> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<QualityInspection>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
