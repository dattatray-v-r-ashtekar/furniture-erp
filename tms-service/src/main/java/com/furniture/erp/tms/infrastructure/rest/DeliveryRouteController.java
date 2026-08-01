package com.furniture.erp.tms.infrastructure.rest;

import com.furniture.erp.tms.application.service.DeliveryRouteService;
import com.furniture.erp.tms.domain.entity.DeliveryRoute;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/tms/routes")
public class DeliveryRouteController {

    private final DeliveryRouteService service;

    public DeliveryRouteController(DeliveryRouteService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<DeliveryRoute> create(@RequestBody CreateRequest request) {
        DeliveryRoute agg = service.createRoute(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeliveryRoute> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<DeliveryRoute>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
