package com.furniture.erp.procurement.infrastructure.rest;

import com.furniture.erp.procurement.application.service.ProcurementService;
import com.furniture.erp.procurement.domain.entity.PurchaseOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/procurement/orders")
public class ProcurementController {

    private final ProcurementService procurementService;

    public ProcurementController(ProcurementService procurementService) {
        this.procurementService = procurementService;
    }

    @PostMapping
    public ResponseEntity<PurchaseOrder> createDraftOrder(@RequestBody CreateOrderRequest request) {
        PurchaseOrder order = procurementService.createDraftOrder(request.vendorId());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/lines")
    public ResponseEntity<Void> addLineItem(@PathVariable UUID orderId, @RequestBody AddLineRequest request) {
        procurementService.addLineItem(orderId, request.skuCode(), request.quantity(), request.unitPrice());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/issue")
    public ResponseEntity<Void> issueOrder(@PathVariable UUID orderId) {
        procurementService.issueOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/receive")
    public ResponseEntity<Void> receiveGoods(@PathVariable UUID orderId) {
        procurementService.receiveGoods(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<PurchaseOrder> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(procurementService.getOrder(orderId));
    }

    @GetMapping
    public ResponseEntity<java.util.List<PurchaseOrder>> getAllOrders() {
        return ResponseEntity.ok(procurementService.getAllOrders());
    }
}

record CreateOrderRequest(String vendorId) {}
record AddLineRequest(String skuCode, Integer quantity, BigDecimal unitPrice) {}
