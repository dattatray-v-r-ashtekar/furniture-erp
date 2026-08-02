package com.furniture.erp.runner;

import com.furniture.erp.accounting.application.service.GeneralLedgerService;
import com.furniture.erp.ecommerce.application.service.OnlineOrderService;
import com.furniture.erp.ecommerce.infrastructure.rest.OnlineOrderController;
import com.furniture.erp.erpcentral.application.service.SalesOrderService;
import com.furniture.erp.erpcentral.domain.event.SalesOrderCreatedEvent;
import com.furniture.erp.inventory.application.service.InventoryService;
import com.furniture.erp.mes.application.service.MesService;
import com.furniture.erp.mes.domain.entity.ProductionOrder;
import com.furniture.erp.mes.domain.entity.ProductionStatus;
import com.furniture.erp.monolith.MonolithApplication;
import com.furniture.erp.tms.application.service.DeliveryRouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = MonolithApplication.class)
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

    @Autowired
    private DeliveryRouteService tmsService;

    @Test
    @DisplayName("E2E Lifecycle: Multi-Item Checkout -> Sales Order -> Accounting Ledger -> MES Manufacturing -> Inventory -> TMS Route Dispatch")
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

        // Step 6: TMS Route Dispatch and Delivery
        var route = tmsService.createRouteWithDetails("ROUTE-E2E-001", "DRV-102 (Express Fleet)", "742 Evergreen Terrace, Springfield", salesOrder.getId().toString());
        assertThat(route.getStatus()).isEqualTo("SCHEDULED");
        assertThat(route.getItems()).hasSize(1);

        tmsService.startRoute(route.getId());
        var startedRoute = tmsService.getById(route.getId());
        assertThat(startedRoute.getStatus()).isEqualTo("IN_TRANSIT");

        var stopId = startedRoute.getItems().get(0).getId();
        tmsService.completeStop(startedRoute.getId(), stopId);
        var deliveredRoute = tmsService.getById(route.getId());
        assertThat(deliveredRoute.getStatus()).isEqualTo("COMPLETED");
    }
}
