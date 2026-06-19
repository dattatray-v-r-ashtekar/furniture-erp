package com.furniture.erp.erpcentral.infrastructure.rest;

import com.furniture.erp.erpcentral.application.service.SalesOrderService;
import com.furniture.erp.erpcentral.domain.entity.SalesOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/erp/sales-orders")
public class SalesOrderController {

    private final SalesOrderService service;

    public SalesOrderController(SalesOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<SalesOrder> create(@RequestBody CreateRequest request) {
        SalesOrder agg = service.createSalesOrder(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SalesOrder> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}

record CreateRequest(String referenceCode) {}
