import os
import re

modules = {
    "erp-central-service": {
        "aggregate": "SalesOrder", "child": "SalesOrderLine", "event": "SalesOrderCreatedEvent",
        "api_path": "/api/v1/erp/sales-orders", "method_name": "createSalesOrder"
    },
    "wms-service": {
        "aggregate": "WarehouseBin", "child": "BinMovement", "event": "BinCapacityReachedEvent",
        "api_path": "/api/v1/wms/bins", "method_name": "createBin"
    },
    "tms-service": {
        "aggregate": "DeliveryRoute", "child": "DeliveryStop", "event": "RouteStartedEvent",
        "api_path": "/api/v1/tms/routes", "method_name": "createRoute"
    },
    "crm-service": {
        "aggregate": "CustomerProfile", "child": "InteractionLog", "event": "CustomerConvertedEvent",
        "api_path": "/api/v1/crm/customers", "method_name": "createCustomer"
    },
    "dealer-portal-service": {
        "aggregate": "WholesaleOrder", "child": "WholesaleItem", "event": "BulkDiscountAppliedEvent",
        "api_path": "/api/v1/dealer/orders", "method_name": "createWholesaleOrder"
    },
    "ecommerce-service": {
        "aggregate": "OnlineOrder", "child": "CartItem", "event": "PaymentProcessedEvent",
        "api_path": "/api/v1/ecommerce/orders", "method_name": "createOnlineOrder"
    },
    "hrms-service": {
        "aggregate": "EmployeeRecord", "child": "AttendanceLog", "event": "ShiftAssignedEvent",
        "api_path": "/api/v1/hrms/employees", "method_name": "createEmployee"
    },
    "payroll-service": {
        "aggregate": "SalarySlip", "child": "TaxDeduction", "event": "SalaryDisbursedEvent",
        "api_path": "/api/v1/payroll/slips", "method_name": "createSalarySlip"
    },
    "accounting-service": {
        "aggregate": "GeneralLedger", "child": "JournalEntry", "event": "LedgerBalancedEvent",
        "api_path": "/api/v1/accounting/ledgers", "method_name": "createLedger"
    },
    "qms-service": {
        "aggregate": "QualityInspection", "child": "DefectLog", "event": "InspectionFailedEvent",
        "api_path": "/api/v1/qms/inspections", "method_name": "createInspection"
    },
    "bi-service": {
        "aggregate": "DashboardReport", "child": "KpiMetric", "event": "ReportGeneratedEvent",
        "api_path": "/api/v1/bi/reports", "method_name": "createReport"
    }
}

aggregate_template = """package com.furniture.erp.{pkg_name}.domain.entity;

import com.furniture.erp.domain.entity.AggregateRoot;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "{table_name_agg}")
public class {aggregate} extends AggregateRoot<UUID> {{

    @Id
    private UUID id;
    private String referenceCode;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "{agg_fk}_id")
    private List<{child}> items = new ArrayList<>();

    protected {aggregate}() {{
    }}

    public {aggregate}(String referenceCode) {{
        this.id = UUID.randomUUID();
        this.referenceCode = referenceCode;
        super.setId(this.id);
    }}

    public UUID getId() {{ return id; }}
    public String getReferenceCode() {{ return referenceCode; }}
    public List<{child}> getItems() {{ return items; }}

    public void addItem({child} item) {{
        this.items.add(item);
    }}
}}
"""

child_template = """package com.furniture.erp.{pkg_name}.domain.entity;

import com.furniture.erp.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

@Entity
@Table(name = "{table_name_child}")
public class {child} extends BaseEntity<UUID> {{

    @Id
    private UUID id;
    private String description;

    protected {child}() {{
    }}

    public {child}(String description) {{
        this.id = UUID.randomUUID();
        this.description = description;
        super.setId(this.id);
    }}

    public UUID getId() {{ return id; }}
    public String getDescription() {{ return description; }}
}}
"""

event_template = """package com.furniture.erp.{pkg_name}.domain.event;

import com.furniture.erp.domain.event.DomainEvent;

import java.time.Instant;
import java.util.UUID;

public class {event} implements DomainEvent<{event}> {{
    private UUID eventId;
    private UUID aggregateId;
    private Instant timestamp;

    public {event}() {{}}

    public {event}(UUID eventId, UUID aggregateId, Instant timestamp) {{
        this.eventId = eventId;
        this.aggregateId = aggregateId;
        this.timestamp = timestamp;
    }}

    public UUID getEventId() {{ return eventId; }}
    public UUID getAggregateId() {{ return aggregateId; }}
    public Instant getTimestamp() {{ return timestamp; }}

    public static {event} create(UUID aggregateId) {{
        return new {event}(UUID.randomUUID(), aggregateId, Instant.now());
    }}
}}
"""

repo_template = """package com.furniture.erp.{pkg_name}.infrastructure.repository;

import com.furniture.erp.{pkg_name}.domain.entity.{aggregate};
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface {aggregate}Repository extends JpaRepository<{aggregate}, UUID> {{
}}
"""

pub_template = """package com.furniture.erp.{pkg_name}.infrastructure.messaging;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaDomainEventPublisher implements DomainEventPublisher<DomainEvent<?>> {{

    private static final Logger log = LoggerFactory.getLogger(KafkaDomainEventPublisher.class);
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaDomainEventPublisher(KafkaTemplate<String, Object> kafkaTemplate) {{
        this.kafkaTemplate = kafkaTemplate;
    }}

    @Override
    public void publish(DomainEvent<?> domainEvent) {{
        String topic = domainEvent.getClass().getSimpleName();
        log.info("Publishing domain event: {{}} to topic: {{}}", domainEvent, topic);
        kafkaTemplate.send(topic, domainEvent);
    }}
}}
"""

service_template = """package com.furniture.erp.{pkg_name}.application.service;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.{pkg_name}.domain.entity.{aggregate};
import com.furniture.erp.{pkg_name}.domain.entity.{child};
import com.furniture.erp.{pkg_name}.domain.event.{event};
import com.furniture.erp.{pkg_name}.infrastructure.repository.{aggregate}Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class {service_name} {{

    private final {aggregate}Repository repository;
    private final DomainEventPublisher<DomainEvent<?>> eventPublisher;

    public {service_name}({aggregate}Repository repository, DomainEventPublisher<DomainEvent<?>> eventPublisher) {{
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }}

    @Transactional
    public {aggregate} {method_name}(String referenceCode) {{
        {aggregate} agg = new {aggregate}(referenceCode);
        agg.addItem(new {child}("Initial item for " + referenceCode));
        {aggregate} saved = repository.save(agg);
        
        eventPublisher.publish({event}.create(saved.getId()));
        return saved;
    }}

    @Transactional(readOnly = true)
    public {aggregate} getById(UUID id) {{
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Not found: " + id));
    }}
}}
"""

controller_template = """package com.furniture.erp.{pkg_name}.infrastructure.rest;

import com.furniture.erp.{pkg_name}.application.service.{service_name};
import com.furniture.erp.{pkg_name}.domain.entity.{aggregate};
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("{api_path}")
public class {controller_name} {{

    private final {service_name} service;

    public {controller_name}({service_name} service) {{
        this.service = service;
    }}

    @PostMapping
    public ResponseEntity<{aggregate}> create(@RequestBody CreateRequest request) {{
        {aggregate} agg = service.{method_name}(request.referenceCode());
        return ResponseEntity.ok(agg);
    }}

    @GetMapping("/{{id}}")
    public ResponseEntity<{aggregate}> get(@PathVariable UUID id) {{
        return ResponseEntity.ok(service.getById(id));
    }}
}}

record CreateRequest(String referenceCode) {{}}
"""

def to_snake_case(name):
    s1 = re.sub('(.)([A-Z][a-z]+)', r'\1_\2', name)
    return re.sub('([a-z0-9])([A-Z])', r'\1_\2', s1).lower()

for module_name, details in modules.items():
    pkg_name = module_name.replace("-service", "").replace("-", "")
    base_dir = f"{module_name}/src/main/java/com/furniture/erp/{pkg_name}"
    
    # Create directories
    os.makedirs(f"{base_dir}/domain/entity", exist_ok=True)
    os.makedirs(f"{base_dir}/domain/event", exist_ok=True)
    os.makedirs(f"{base_dir}/infrastructure/repository", exist_ok=True)
    os.makedirs(f"{base_dir}/infrastructure/messaging", exist_ok=True)
    os.makedirs(f"{base_dir}/application/service", exist_ok=True)
    os.makedirs(f"{base_dir}/infrastructure/rest", exist_ok=True)

    agg = details["aggregate"]
    child = details["child"]
    event = details["event"]
    service_name = f"{agg}Service"
    controller_name = f"{agg}Controller"
    
    table_name_agg = to_snake_case(agg) + "s"
    table_name_child = to_snake_case(child) + "s"
    agg_fk = to_snake_case(agg)

    # Write files
    with open(f"{base_dir}/domain/entity/{agg}.java", "w") as f:
        f.write(aggregate_template.format(pkg_name=pkg_name, aggregate=agg, child=child, table_name_agg=table_name_agg, agg_fk=agg_fk))
        
    with open(f"{base_dir}/domain/entity/{child}.java", "w") as f:
        f.write(child_template.format(pkg_name=pkg_name, child=child, table_name_child=table_name_child))

    with open(f"{base_dir}/domain/event/{event}.java", "w") as f:
        f.write(event_template.format(pkg_name=pkg_name, event=event))
        
    with open(f"{base_dir}/infrastructure/repository/{agg}Repository.java", "w") as f:
        f.write(repo_template.format(pkg_name=pkg_name, aggregate=agg))

    with open(f"{base_dir}/infrastructure/messaging/KafkaDomainEventPublisher.java", "w") as f:
        f.write(pub_template.format(pkg_name=pkg_name))

    with open(f"{base_dir}/application/service/{service_name}.java", "w") as f:
        f.write(service_template.format(pkg_name=pkg_name, aggregate=agg, child=child, event=event, service_name=service_name, method_name=details["method_name"]))

    with open(f"{base_dir}/infrastructure/rest/{controller_name}.java", "w") as f:
        f.write(controller_template.format(pkg_name=pkg_name, aggregate=agg, service_name=service_name, controller_name=controller_name, api_path=details["api_path"], method_name=details["method_name"]))

print("Generated business logic for 11 modules successfully.")
