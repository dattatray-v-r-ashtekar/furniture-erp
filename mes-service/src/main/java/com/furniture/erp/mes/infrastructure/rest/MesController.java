package com.furniture.erp.mes.infrastructure.rest;

import com.furniture.erp.mes.application.service.MesService;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/mes/orders")
public class MesController {

    private final MesService mesService;

    public MesController(MesService mesService) {
        this.mesService = mesService;
    }

    @PostMapping
    public ResponseEntity<ProductionOrder> planProduction(@RequestBody PlanProductionRequest request) {
        ProductionOrder order = mesService.planProduction(request.productSku(), request.targetQuantity());
        return ResponseEntity.ok(order);
    }

    @PostMapping("/{orderId}/start")
    public ResponseEntity<Void> startProductionOrder(@PathVariable UUID orderId) {
        mesService.startProductionOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/work-orders/{workOrderId}/start")
    public ResponseEntity<Void> startWorkOrder(@PathVariable UUID orderId, @PathVariable UUID workOrderId) {
        mesService.startWorkOrder(orderId, workOrderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/work-orders/{workOrderId}/report")
    public ResponseEntity<Void> reportProgress(
            @PathVariable UUID orderId,
            @PathVariable UUID workOrderId,
            @RequestBody ReportProgressRequest request) {
        mesService.reportWorkOrderProgress(orderId, workOrderId, request.goodQuantity(), request.defectiveQuantity());
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/work-orders/{workOrderId}/complete")
    public ResponseEntity<Void> completeWorkOrder(@PathVariable UUID orderId, @PathVariable UUID workOrderId) {
        mesService.completeWorkOrder(orderId, workOrderId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<Void> completeProductionOrder(@PathVariable UUID orderId) {
        mesService.completeProductionOrder(orderId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ProductionOrder> getOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(mesService.getOrder(orderId));
    }

    @GetMapping
    public ResponseEntity<java.util.List<ProductionOrder>> getAllOrders() {
        return ResponseEntity.ok(mesService.getAllOrders());
    }
}

record PlanProductionRequest(String productSku, Integer targetQuantity) {}
record ReportProgressRequest(Integer goodQuantity, Integer defectiveQuantity) {}
