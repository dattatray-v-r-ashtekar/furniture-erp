package com.furniture.erp.accounting.infrastructure.rest;

import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.accounting.domain.entity.GeneralLedger;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounting/ledgers")
public class GeneralLedgerController {

    private final GeneralLedgerService service;

    public GeneralLedgerController(GeneralLedgerService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GeneralLedger> create(@RequestBody CreateRequest request) {
        GeneralLedger agg = service.createLedger(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GeneralLedger> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<GeneralLedger>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
