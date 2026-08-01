package com.furniture.erp.bi.infrastructure.rest;

import com.furniture.erp.bi.application.service.DashboardReportService;
import com.furniture.erp.bi.domain.entity.DashboardReport;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/bi/reports")
public class DashboardReportController {

    private final DashboardReportService service;

    public DashboardReportController(DashboardReportService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DashboardReport> create(@RequestBody CreateRequest request) {
        DashboardReport agg = service.createReport(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DashboardReport> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<DashboardReport>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
