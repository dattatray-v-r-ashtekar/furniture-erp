package com.furniture.erp.dealerportal.infrastructure.rest;

import com.furniture.erp.dealerportal.application.service.WholesaleOrderService;
import com.furniture.erp.dealerportal.domain.entity.WholesaleOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/dealer/orders")
public class WholesaleOrderController {

    private final WholesaleOrderService service;

    public WholesaleOrderController(WholesaleOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<WholesaleOrder> create(@RequestBody CreateRequest request) {
        WholesaleOrder agg = service.createWholesaleOrder(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<WholesaleOrder> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<WholesaleOrder>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
