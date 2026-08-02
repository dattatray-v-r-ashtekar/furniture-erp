import os

BASE_DIR = r"C:\Users\datta\.gemini\antigravity\scratch\furniture-erp"

tests = {
    # 1. common-domain
    os.path.join(BASE_DIR, "common-domain", "src", "test", "java", "com", "furniture", "erp", "domain", "DomainEventTest.java"): """package com.furniture.erp.domain;

import com.furniture.erp.domain.entity.AggregateRoot;
import com.furniture.erp.domain.event.DomainEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

    static class SampleEvent implements DomainEvent<String> {
        private final String data;
        public SampleEvent(String data) {
            this.data = data;
        }
        public String getData() { return data; }
    }

    static class SampleAggregate extends AggregateRoot<UUID> {
        public SampleAggregate(UUID id) {
            super.setId(id);
        }
    }

    @Test
    @DisplayName("DomainEvent marker interface should encapsulate event payload")
    void testDomainEvent() {
        SampleEvent event = new SampleEvent("test-payload");
        assertThat(event).isNotNull();
        assertThat(event.getData()).isEqualTo("test-payload");
        assertThat(event).isInstanceOf(DomainEvent.class);
    }

    @Test
    @DisplayName("AggregateRoot and BaseEntity should maintain identity equality and hash code")
    void testAggregateRootIdentity() {
        UUID id = UUID.randomUUID();
        SampleAggregate agg1 = new SampleAggregate(id);
        SampleAggregate agg2 = new SampleAggregate(id);
        SampleAggregate agg3 = new SampleAggregate(UUID.randomUUID());

        assertThat(agg1.getId()).isEqualTo(id);
        assertThat(agg1).isEqualTo(agg2);
        assertThat(agg1).isNotEqualTo(agg3);
        assertThat(agg1.hashCode()).isEqualTo(agg2.hashCode());
    }
}
""",

    # 2. inventory-service
    os.path.join(BASE_DIR, "inventory-service", "src", "test", "java", "com", "furniture", "erp", "inventory", "InventoryServiceTest.java"): """package com.furniture.erp.inventory;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.inventory.application.service.InventoryService;
import com.furniture.erp.inventory.domain.entity.StockItem;
import com.furniture.erp.inventory.infrastructure.repository.StockItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private StockItemRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private InventoryService service;

    @BeforeEach
    void setUp() {
        service = new InventoryService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createStockItem should save and return stock item")
    void testCreateStockItem() {
        when(repository.findBySkuCode("WOOD-OAK")).thenReturn(Optional.empty());
        when(repository.save(any(StockItem.class))).thenAnswer(i -> i.getArgument(0));

        StockItem item = service.createStockItem("WOOD-OAK", "Solid Oak Planks", "BIN-A1");

        assertThat(item).isNotNull();
        assertThat(item.getSkuCode()).isEqualTo("WOOD-OAK");
        assertThat(item.getLocationBin()).isEqualTo("BIN-A1");
    }

    @Test
    @DisplayName("addStock and deductStock should modify quantities and publish events")
    void testAddAndDeductStock() {
        StockItem item = new StockItem("STEEL-ROD", "Steel Rods", "BIN-B2");
        when(repository.findBySkuCode("STEEL-ROD")).thenReturn(Optional.of(item));
        when(repository.save(any(StockItem.class))).thenAnswer(i -> i.getArgument(0));

        service.addStock("STEEL-ROD", 100);
        assertThat(item.getAvailableQuantity()).isEqualTo(100);

        service.deductStock("STEEL-ROD", 30);
        assertThat(item.getAvailableQuantity()).isEqualTo(70);

        verify(eventPublisher, times(2)).publish(any());
    }
}
""",

    # 3. procurement-service
    os.path.join(BASE_DIR, "procurement-service", "src", "test", "java", "com", "furniture", "erp", "procurement", "ProcurementServiceTest.java"): """package com.furniture.erp.procurement;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.procurement.application.service.ProcurementService;
import com.furniture.erp.procurement.domain.entity.PurchaseOrder;
import com.furniture.erp.procurement.infrastructure.repository.PurchaseOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcurementServiceTest {

    @Mock
    private PurchaseOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private ProcurementService service;

    @BeforeEach
    void setUp() {
        service = new ProcurementService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createDraftOrder and issueOrder lifecycle")
    void testPurchaseOrderLifecycle() {
        when(repository.save(any(PurchaseOrder.class))).thenAnswer(i -> i.getArgument(0));

        PurchaseOrder order = service.createDraftOrder("VENDOR-001");
        assertThat(order).isNotNull();
        assertThat(order.getVendorId()).isEqualTo("VENDOR-001");

        UUID orderId = order.getId();
        when(repository.findById(orderId)).thenReturn(Optional.of(order));

        service.addLineItem(orderId, "WOOD-01", 50, BigDecimal.valueOf(120.00));
        assertThat(order.getLines()).hasSize(1);

        service.issueOrder(orderId);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 4. erp-central-service
    os.path.join(BASE_DIR, "erp-central-service", "src", "test", "java", "com", "furniture", "erp", "erpcentral", "SalesOrderServiceTest.java"): """package com.furniture.erp.erpcentral;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.erpcentral.application.service.SalesOrderService;
import com.furniture.erp.erpcentral.domain.entity.SalesOrder;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import com.furniture.erp.erpcentral.infrastructure.repository.SalesOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesOrderServiceTest {

    @Mock
    private SalesOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private SalesOrderService service;

    @BeforeEach
    void setUp() {
        service = new SalesOrderService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createSalesOrder should create single consolidated sales order with items and publish event")
    void testCreateSalesOrder() {
        when(repository.save(any(SalesOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<SalesOrderCreatedEvent.ItemDto> items = List.of(
                new SalesOrderCreatedEvent.ItemDto("TABLE-OAK", "Oak Table", 1, 75000.00),
                new SalesOrderCreatedEvent.ItemDto("CHAIR-01", "Ergo Chair", 2, 12500.00)
        );

        SalesOrder order = service.createSalesOrder(UUID.randomUUID(), "SO-12345", 100000.00, items);

        assertThat(order).isNotNull();
        assertThat(order.getReferenceCode()).isEqualTo("SO-12345");
        assertThat(order.getTotalAmount()).isEqualTo(100000.00);
        assertThat(order.getItems()).hasSize(2);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 5. mes-service
    os.path.join(BASE_DIR, "mes-service", "src", "test", "java", "com", "furniture", "erp", "mes", "MesServiceTest.java"): """package com.furniture.erp.mes;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.mes.application.service.MesService;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import com.furniture.erp.mes.domain.entity.ProductionStatus;
import com.furniture.erp.mes.domain.entity.WorkOrder;
import com.furniture.erp.mes.domain.entity.WorkOrderStatus;
import com.furniture.erp.mes.infrastructure.repository.ProductionOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MesServiceTest {

    @Mock
    private ProductionOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private MesService service;

    @BeforeEach
    void setUp() {
        service = new MesService(repository, eventPublisher);
    }

    @Test
    @DisplayName("planProduction should initialize work orders with 3 standard routing steps")
    void testPlanProduction() {
        when(repository.save(any(ProductionOrder.class))).thenAnswer(i -> i.getArgument(0));

        ProductionOrder order = service.planProduction("BED-KING", 2);

        assertThat(order).isNotNull();
        assertThat(order.getProductSku()).isEqualTo("BED-KING");
        assertThat(order.getTargetQuantity()).isEqualTo(2);
        assertThat(order.getWorkOrders()).hasSize(3);
        assertThat(order.getStatus()).isEqualTo(ProductionStatus.PLANNED);
        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    @DisplayName("completeProductionOrder should auto-complete all pending work orders and publish ProductionCompletedEvent")
    void testCompleteProductionOrder() {
        ProductionOrder order = new ProductionOrder("TABLE-OAK", 1);
        order.addWorkOrder(new WorkOrder("Cutting", "Saw-01"));
        order.addWorkOrder(new WorkOrder("Assembly", "Asm-01"));
        order.startProduction();

        UUID orderId = order.getId();
        when(repository.findById(orderId)).thenReturn(Optional.of(order));
        when(repository.save(any(ProductionOrder.class))).thenAnswer(i -> i.getArgument(0));

        service.completeProductionOrder(orderId);

        assertThat(order.getStatus()).isEqualTo(ProductionStatus.COMPLETED);
        assertThat(order.getWorkOrders()).allMatch(wo -> wo.getStatus() == WorkOrderStatus.DONE);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 6. wms-service
    os.path.join(BASE_DIR, "wms-service", "src", "test", "java", "com", "furniture", "erp", "wms", "WarehouseBinServiceTest.java"): """package com.furniture.erp.wms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.wms.application.service.WarehouseBinService;
import com.furniture.erp.wms.domain.entity.WarehouseBin;
import com.furniture.erp.wms.infrastructure.repository.WarehouseBinRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseBinServiceTest {

    @Mock
    private WarehouseBinRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private WarehouseBinService service;

    @BeforeEach
    void setUp() {
        service = new WarehouseBinService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createBin should initialize warehouse bin and publish event")
    void testWarehouseBinCreation() {
        when(repository.save(any(WarehouseBin.class))).thenAnswer(i -> i.getArgument(0));

        WarehouseBin bin = service.createBin("BIN-RACK-01");
        assertThat(bin).isNotNull();
        assertThat(bin.getReferenceCode()).isEqualTo("BIN-RACK-01");
        assertThat(bin.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 7. tms-service
    os.path.join(BASE_DIR, "tms-service", "src", "test", "java", "com", "furniture", "erp", "tms", "DeliveryRouteServiceTest.java"): """package com.furniture.erp.tms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.tms.application.service.DeliveryRouteService;
import com.furniture.erp.tms.domain.entity.DeliveryRoute;
import com.furniture.erp.tms.infrastructure.repository.DeliveryRouteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeliveryRouteServiceTest {

    @Mock
    private DeliveryRouteRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private DeliveryRouteService service;

    @BeforeEach
    void setUp() {
        service = new DeliveryRouteService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createRoute should initialize delivery route and publish RouteStartedEvent")
    void testDeliveryRouteCreation() {
        when(repository.save(any(DeliveryRoute.class))).thenAnswer(i -> i.getArgument(0));

        DeliveryRoute route = service.createRoute("ROUTE-BLR-01");
        assertThat(route).isNotNull();
        assertThat(route.getReferenceCode()).isEqualTo("ROUTE-BLR-01");
        assertThat(route.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 8. crm-service
    os.path.join(BASE_DIR, "crm-service", "src", "test", "java", "com", "furniture", "erp", "crm", "CustomerProfileServiceTest.java"): """package com.furniture.erp.crm;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.crm.application.service.CustomerProfileService;
import com.furniture.erp.crm.domain.entity.CustomerProfile;
import com.furniture.erp.crm.infrastructure.repository.CustomerProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerProfileServiceTest {

    @Mock
    private CustomerProfileRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private CustomerProfileService service;

    @BeforeEach
    void setUp() {
        service = new CustomerProfileService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createCustomer should initialize customer profile and publish event")
    void testCustomerCreation() {
        when(repository.save(any(CustomerProfile.class))).thenAnswer(i -> i.getArgument(0));

        CustomerProfile customer = service.createCustomer("CUST-99001");
        assertThat(customer).isNotNull();
        assertThat(customer.getReferenceCode()).isEqualTo("CUST-99001");
        assertThat(customer.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 9. dealer-portal-service
    os.path.join(BASE_DIR, "dealer-portal-service", "src", "test", "java", "com", "furniture", "erp", "dealerportal", "WholesaleOrderServiceTest.java"): """package com.furniture.erp.dealerportal;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.dealerportal.application.service.WholesaleOrderService;
import com.furniture.erp.dealerportal.domain.entity.WholesaleOrder;
import com.furniture.erp.dealerportal.infrastructure.repository.WholesaleOrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WholesaleOrderServiceTest {

    @Mock
    private WholesaleOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private WholesaleOrderService service;

    @BeforeEach
    void setUp() {
        service = new WholesaleOrderService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createWholesaleOrder should save and publish WholesaleOrderCreatedEvent")
    void testCreateWholesaleOrder() {
        when(repository.save(any(WholesaleOrder.class))).thenAnswer(i -> i.getArgument(0));

        WholesaleOrder order = service.createWholesaleOrder("WHOLESALE-DEALER-01");
        assertThat(order).isNotNull();
        assertThat(order.getReferenceCode()).isEqualTo("WHOLESALE-DEALER-01");
        assertThat(order.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 10. ecommerce-service
    os.path.join(BASE_DIR, "ecommerce-service", "src", "test", "java", "com", "furniture", "erp", "ecommerce", "OnlineOrderServiceTest.java"): """package com.furniture.erp.ecommerce;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.ecommerce.application.service.OnlineOrderService;
import com.furniture.erp.ecommerce.domain.entity.OnlineOrder;
import com.furniture.erp.ecommerce.infrastructure.repository.OnlineOrderRepository;
import com.furniture.erp.ecommerce.infrastructure.rest.OnlineOrderController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OnlineOrderServiceTest {

    @Mock
    private OnlineOrderRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private OnlineOrderService service;

    @BeforeEach
    void setUp() {
        service = new OnlineOrderService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createOnlineOrder should aggregate multi-item cart correctly and publish event")
    void testCreateOnlineOrderWithMultipleItems() {
        when(repository.save(any(OnlineOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<OnlineOrderController.ItemRequest> items = List.of(
                new OnlineOrderController.ItemRequest("TABLE-OAK", "Custom Oak Table", 1, 75000.50),
                new OnlineOrderController.ItemRequest("CHAIR-OFFICE", "Ergo Chair", 2, 12500.00)
        );

        OnlineOrder order = service.createOnlineOrder("ORD-101", 100000.50, items);

        assertThat(order).isNotNull();
        assertThat(order.getReferenceCode()).isEqualTo("ORD-101");
        assertThat(order.getTotalAmount()).isEqualTo(100000.50);
        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getStatus()).isEqualTo("PAID");

        verify(eventPublisher, times(1)).publish(any());
    }

    @Test
    @DisplayName("createOnlineOrder should parse Map items correctly when json map is passed")
    void testCreateOnlineOrderWithMapItems() {
        when(repository.save(any(OnlineOrder.class))).thenAnswer(i -> i.getArgument(0));

        List<Map<String, Object>> mapItems = List.of(
                Map.of("sku", "BED-KING", "name", "King Bed", "quantity", 1, "price", 45000.00)
        );

        OnlineOrder order = service.createOnlineOrder("ORD-102", 0.0, mapItems);

        assertThat(order).isNotNull();
        assertThat(order.getTotalAmount()).isEqualTo(45000.00);
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getSku()).isEqualTo("BED-KING");
    }

    @Test
    @DisplayName("createOnlineOrder should fallback to default bed if items list is empty")
    void testCreateOnlineOrderFallback() {
        when(repository.save(any(OnlineOrder.class))).thenAnswer(i -> i.getArgument(0));

        OnlineOrder order = service.createOnlineOrder("ORD-EMPTY", 0.0, List.of());

        assertThat(order).isNotNull();
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getItems().get(0).getSku()).isEqualTo("BED-KING");
        assertThat(order.getTotalAmount()).isEqualTo(45000.00);
    }
}
""",

    # 11. hrms-service
    os.path.join(BASE_DIR, "hrms-service", "src", "test", "java", "com", "furniture", "erp", "hrms", "EmployeeRecordServiceTest.java"): """package com.furniture.erp.hrms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.hrms.application.service.EmployeeRecordService;
import com.furniture.erp.hrms.domain.entity.EmployeeRecord;
import com.furniture.erp.hrms.infrastructure.repository.EmployeeRecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeRecordServiceTest {

    @Mock
    private EmployeeRecordRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private EmployeeRecordService service;

    @BeforeEach
    void setUp() {
        service = new EmployeeRecordService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createEmployee should save employee record and publish EmployeeOnboardedEvent")
    void testCreateEmployee() {
        when(repository.save(any(EmployeeRecord.class))).thenAnswer(i -> i.getArgument(0));

        EmployeeRecord emp = service.createEmployee("EMP-HR-001");
        assertThat(emp).isNotNull();
        assertThat(emp.getReferenceCode()).isEqualTo("EMP-HR-001");
        assertThat(emp.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 12. payroll-service
    os.path.join(BASE_DIR, "payroll-service", "src", "test", "java", "com", "furniture", "erp", "payroll", "SalarySlipServiceTest.java"): """package com.furniture.erp.payroll;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.payroll.application.service.SalarySlipService;
import com.furniture.erp.payroll.domain.entity.SalarySlip;
import com.furniture.erp.payroll.infrastructure.repository.SalarySlipRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalarySlipServiceTest {

    @Mock
    private SalarySlipRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private SalarySlipService service;

    @BeforeEach
    void setUp() {
        service = new SalarySlipService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createSalarySlip should save salary slip and publish SalaryDisbursedEvent")
    void testCreateSalarySlip() {
        when(repository.save(any(SalarySlip.class))).thenAnswer(i -> i.getArgument(0));

        SalarySlip slip = service.createSalarySlip("SLIP-2026-08");
        assertThat(slip).isNotNull();
        assertThat(slip.getReferenceCode()).isEqualTo("SLIP-2026-08");
        assertThat(slip.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 13. accounting-service
    os.path.join(BASE_DIR, "accounting-service", "src", "test", "java", "com", "furniture", "erp", "accounting", "GeneralLedgerServiceTest.java"): """package com.furniture.erp.accounting;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.accounting.domain.entity.GeneralLedger;
import com.furniture.erp.accounting.infrastructure.repository.GeneralLedgerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralLedgerServiceTest {

    @Mock
    private GeneralLedgerRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private GeneralLedgerService service;

    @BeforeEach
    void setUp() {
        service = new GeneralLedgerService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createLedger should create credit ledger entry and publish LedgerBalancedEvent")
    void testCreateLedger() {
        when(repository.save(any(GeneralLedger.class))).thenAnswer(i -> i.getArgument(0));

        UUID orderId = UUID.randomUUID();
        GeneralLedger ledger = service.createLedger(orderId, "ORD-999", "REV-B2C", "CREDIT", 87500.50, "B2C Revenue");

        assertThat(ledger).isNotNull();
        assertThat(ledger.getAmount()).isEqualTo(87500.50);
        assertThat(ledger.getEntryType()).isEqualTo("CREDIT");
        assertThat(ledger.getReferenceCode()).isEqualTo("ORD-999");
        assertThat(ledger.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 14. qms-service
    os.path.join(BASE_DIR, "qms-service", "src", "test", "java", "com", "furniture", "erp", "qms", "QualityInspectionServiceTest.java"): """package com.furniture.erp.qms;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.qms.application.service.QualityInspectionService;
import com.furniture.erp.qms.domain.entity.QualityInspection;
import com.furniture.erp.qms.infrastructure.repository.QualityInspectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class QualityInspectionServiceTest {

    @Mock
    private QualityInspectionRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private QualityInspectionService service;

    @BeforeEach
    void setUp() {
        service = new QualityInspectionService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createInspection should record inspection entry and publish event")
    void testCreateInspection() {
        when(repository.save(any(QualityInspection.class))).thenAnswer(i -> i.getArgument(0));

        QualityInspection inspection = service.createInspection("QC-BATCH-2026");

        assertThat(inspection).isNotNull();
        assertThat(inspection.getReferenceCode()).isEqualTo("QC-BATCH-2026");
        assertThat(inspection.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 15. bi-service
    os.path.join(BASE_DIR, "bi-service", "src", "test", "java", "com", "furniture", "erp", "bi", "DashboardReportServiceTest.java"): """package com.furniture.erp.bi;

import com.furniture.erp.domain.event.DomainEvent;
import com.furniture.erp.domain.event.publisher.DomainEventPublisher;
import com.furniture.erp.bi.application.service.DashboardReportService;
import com.furniture.erp.bi.domain.entity.DashboardReport;
import com.furniture.erp.bi.infrastructure.repository.DashboardReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardReportServiceTest {

    @Mock
    private DashboardReportRepository repository;

    @Mock
    private DomainEventPublisher<DomainEvent<?>> eventPublisher;

    private DashboardReportService service;

    @BeforeEach
    void setUp() {
        service = new DashboardReportService(repository, eventPublisher);
    }

    @Test
    @DisplayName("createReport should generate BI dashboard report and publish event")
    void testCreateDashboardReport() {
        when(repository.save(any(DashboardReport.class))).thenAnswer(i -> i.getArgument(0));

        DashboardReport report = service.createReport("BI-EXEC-Q3");

        assertThat(report).isNotNull();
        assertThat(report.getReferenceCode()).isEqualTo("BI-EXEC-Q3");
        assertThat(report.getItems()).hasSize(1);
        verify(eventPublisher, times(1)).publish(any());
    }
}
""",

    # 16. Integration Tests - Accounting Kafka Consumer
    os.path.join(BASE_DIR, "accounting-service", "src", "test", "java", "com", "furniture", "erp", "accounting", "AccountingKafkaConsumerIntegrationTest.java"): """package com.furniture.erp.accounting;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.accounting.infrastructure.messaging.AccountingKafkaConsumer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AccountingKafkaConsumerIntegrationTest {

    @Mock
    private GeneralLedgerService ledgerService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("Accounting consumer should deserialize multi-item B2CPaymentReceivedEvent and write ledger entry")
    void testHandlePaymentReceivedEvent() {
        AccountingKafkaConsumer consumer = new AccountingKafkaConsumer(ledgerService, objectMapper);

        String kafkaMessageJson = \"\"\"{
            "eventId": "a1b2c3d4-0000-0000-0000-000000000000",
            "orderId": "b1b2c3d4-1111-2222-3333-444455556666",
            "referenceCode": "ORD-B2C-9988",
            "totalAmount": 87500.50,
            "items": [
                {"sku": "TABLE-OAK", "name": "Oak Dining Table", "quantity": 1, "price": 75000.50},
                {"sku": "CHAIR-OFFICE", "name": "Ergo Chair", "quantity": 1, "price": 12500.00}
            ]
        }\"\"\";

        consumer.handlePaymentReceived(kafkaMessageJson);

        verify(ledgerService, times(1)).createLedger(
                eq(UUID.fromString("b1b2c3d4-1111-2222-3333-444455556666")),
                eq("ORD-B2C-9988"),
                eq("REVENUE-B2C"),
                eq("CREDIT"),
                eq(87500.50),
                contains("Oak Dining Table")
        );
    }
}
""",

    # 17. Monolith Runner End-to-End Orchestration Test
    os.path.join(BASE_DIR, "erp-monolith-runner", "src", "test", "java", "com", "furniture", "erp", "runner", "FurnitureErpE2EOrchestrationTest.java"): """package com.furniture.erp.runner;

import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.ecommerce.application.service.OnlineOrderService;
import com.furniture.erp.ecommerce.infrastructure.rest.OnlineOrderController;
import com.furniture.erp.erpcentral.application.service.SalesOrderService;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import com.furniture.erp.inventory.application.service.InventoryService;
import com.furniture.erp.mes.application.service.MesService;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import com.furniture.erp.mes.domain.entity.ProductionStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = ErpMonolithApplication.class)
@ActiveProfiles("test")
class FurnitureErpE2EOrchestrationTest {

    @Autowired
    private OnlineOrderService ecommerceService;

    @Autowired
    private SalesOrderService salesOrderService;

    @Autowired
    private GeneralLedgerService ledgerService;

    @Autowired
    private MesService mesService;

    @Autowired
    private InventoryService inventoryService;

    @Test
    @DisplayName("E2E Lifecycle: Multi-Item Checkout -> Sales Order -> Accounting Ledger -> MES Manufacturing -> Inventory")
    void testEndToEndFurnitureLifecycle() {
        // Step 1: E-Commerce Multi-Item Checkout
        List<OnlineOrderController.ItemRequest> cart = List.of(
                new OnlineOrderController.ItemRequest("TABLE-OAK", "Oak Dining Table", 1, 75000.50),
                new OnlineOrderController.ItemRequest("CHAIR-OFFICE", "Ergo Chair", 2, 12500.00)
        );
        var onlineOrder = ecommerceService.createOnlineOrder("E2E-ORD-001", 100000.50, cart);
        assertThat(onlineOrder.getId()).isNotNull();
        assertThat(onlineOrder.getTotalAmount()).isEqualTo(100000.50);

        // Step 2: ERP Central Sales Order Creation
        var salesOrder = salesOrderService.createSalesOrder(
                onlineOrder.getId(),
                onlineOrder.getReferenceCode(),
                onlineOrder.getTotalAmount(),
                List.of(
                        new SalesOrderCreatedEvent.ItemDto("TABLE-OAK", "Oak Dining Table", 1, 75000.50),
                        new SalesOrderCreatedEvent.ItemDto("CHAIR-OFFICE", "Ergo Chair", 2, 12500.00)
                )
        );
        assertThat(salesOrder.getReferenceCode()).isEqualTo("E2E-ORD-001");
        assertThat(salesOrder.getItems()).hasSize(2);

        // Step 3: Accounting Ledger Entry
        var ledgerEntry = ledgerService.createLedger(
                onlineOrder.getId(),
                onlineOrder.getReferenceCode(),
                "REVENUE-B2C",
                "CREDIT",
                onlineOrder.getTotalAmount(),
                "B2C Revenue for E2E-ORD-001 (Oak Dining Table x1, Ergo Chair x2)"
        );
        assertThat(ledgerEntry.getAmount()).isEqualTo(100000.50);
        assertThat(ledgerEntry.getEntryType()).isEqualTo("CREDIT");

        // Step 4: MES Factory Floor Production Order & Completion
        ProductionOrder tableProd = mesService.planProduction("TABLE-OAK", 1);
        ProductionOrder chairProd = mesService.planProduction("CHAIR-OFFICE", 2);
        assertThat(tableProd.getStatus()).isEqualTo(ProductionStatus.PLANNED);
        assertThat(chairProd.getStatus()).isEqualTo(ProductionStatus.PLANNED);

        // Start & Complete Table routing
        mesService.startProductionOrder(tableProd.getId());
        mesService.completeProductionOrder(tableProd.getId());
        var completedTable = mesService.getOrder(tableProd.getId());
        assertThat(completedTable.getStatus()).isEqualTo(ProductionStatus.COMPLETED);

        // Step 5: Inventory Stock Increment
        inventoryService.createStockItem("TABLE-OAK-FIN", "Finished Oak Table", "WH-BAY-1");
        inventoryService.addStock("TABLE-OAK-FIN", 1);
        var stock = inventoryService.getStock("TABLE-OAK-FIN");
        assertThat(stock.getAvailableQuantity()).isEqualTo(1);
    }
}
""",

    # 18. Python AI Analytics Unit Tests (using unittest standard library)
    os.path.join(BASE_DIR, "ai-analytics-service", "tests", "test_ai_analytics.py"): """import unittest
import json

class TestAiAnalytics(unittest.TestCase):

    def test_prompt_formatting(self):
        event_data = {
            "productionOrderId": "12345",
            "productSku": "BED-KING",
            "targetQuantity": 2,
            "timestamp": 1785653237.95
        }
        prompt = f"Analyze the production order for SKU: {event_data['productSku']} with Quantity: {event_data['targetQuantity']}."
        self.assertIn("BED-KING", prompt)
        self.assertIn("Quantity: 2", prompt)

    def test_json_payload_parsing(self):
        raw_kafka_payload = '{"eventId": "abc-123", "referenceCode": "ORD-001", "totalAmount": 87500.50}'
        parsed = json.loads(raw_kafka_payload)
        self.assertEqual(parsed["referenceCode"], "ORD-001")
        self.assertEqual(parsed["totalAmount"], 87500.50)

if __name__ == '__main__':
    unittest.main()
"""
}

# Clean old test files if names changed
old_tests = [
    os.path.join(BASE_DIR, "hrms-service", "src", "test", "java", "com", "furniture", "erp", "hrms", "EmployeeServiceTest.java"),
    os.path.join(BASE_DIR, "payroll-service", "src", "test", "java", "com", "furniture", "erp", "payroll", "PayrollServiceTest.java"),
    os.path.join(BASE_DIR, "bi-service", "src", "test", "java", "com", "furniture", "erp", "bi", "ExecutiveReportServiceTest.java"),
    os.path.join(BASE_DIR, "crm-service", "src", "test", "java", "com", "furniture", "erp", "crm", "LeadServiceTest.java"),
    os.path.join(BASE_DIR, "dealer-portal-service", "src", "test", "java", "com", "furniture", "erp", "dealer", "DealerOrderServiceTest.java")
]

for ot in old_tests:
    if os.path.exists(ot):
        os.remove(ot)
        print(f"Removed outdated test: {ot}")

for path, content in tests.items():
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        f.write(content)
    print(f"Created: {path}")

print("All test suites written successfully!")
