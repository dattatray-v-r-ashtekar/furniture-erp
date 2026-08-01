package com.furniture.erp.wms.infrastructure.rest;

import com.furniture.erp.wms.application.service.WarehouseBinService;
import com.furniture.erp.wms.domain.entity.WarehouseBin;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/wms/bins")
public class WarehouseBinController {

    private final WarehouseBinService service;

    public WarehouseBinController(WarehouseBinService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WarehouseBin> create(@RequestBody CreateRequest request) {
        WarehouseBin agg = service.createBin(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WarehouseBin> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<WarehouseBin>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
