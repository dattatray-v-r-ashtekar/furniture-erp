package com.furniture.erp.crm.infrastructure.rest;

import com.furniture.erp.crm.application.service.CustomerProfileService;
import com.furniture.erp.crm.domain.entity.CustomerProfile;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/crm/customers")
public class CustomerProfileController {

    private final CustomerProfileService service;

    public CustomerProfileController(CustomerProfileService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CustomerProfile> create(@RequestBody CreateRequest request) {
        CustomerProfile agg = service.createCustomer(request.referenceCode());
        return ResponseEntity.ok(agg);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerProfile> get(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }


    @GetMapping
    public ResponseEntity<List<CustomerProfile>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }
}

record CreateRequest(String referenceCode) {}
