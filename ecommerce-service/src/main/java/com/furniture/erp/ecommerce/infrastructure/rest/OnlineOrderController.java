package com.furniture.erp.ecommerce.infrastructure.rest;

import com.furniture.erp.ecommerce.application.service.OnlineOrderService;
import com.furniture.erp.ecommerce.domain.entity.OnlineOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ecommerce/orders")
public class OnlineOrderController {

    private final OnlineOrderService service;

    public OnlineOrderController(OnlineOrderService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<OnlineOrder> create(@RequestBody CreateRequest request) {
        OnlineOrder agg = service.createOnlineOrder(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OnlineOrder> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }
}

record CreateRequest(String referenceCode) {}
