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
import com.furniture.erp.mes.domain.entity.WorkOrder;
import com.furniture.erp.monolith.MonolithApplication;
import com.furniture.erp.tms.application.service.DeliveryRouteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

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
    @DisplayName("E2E Lifecycle: Multi-Item Checkout -> Sales Order -> Accounting Ledger -> MES Search & Manufacturing -> Inventory -> TMS Route Dispatch")
    void testEndToEndFurnitureLifecycle() {
        // Step 1: E-Commerce Multi-Item Checkout
        String orderRef = "ORD-B2C-916370";
        List<OnlineOrderController.ItemRequest> cart = List.of(
                new OnlineOrderController.ItemRequest("TABLE-OAK", "Custom Oak Dining Table", 1, 75000.50),
                new OnlineOrderController.ItemRequest("CHAIR-OFFICE", "Ergo Office Chair", 1, 12500.00)
        );
        var onlineOrder = ecommerceService.createOnlineOrder(orderRef, 87500.50, cart);
        assertThat(onlineOrder.getId()).isNotNull();
        assertThat(onlineOrder.getReferenceCode()).isEqualTo(orderRef);
        assertThat(onlineOrder.getTotalAmount()).isEqualTo(87500.50);

        UUID orderUuid = onlineOrder.getId();
        String orderUuidStr = orderUuid.toString();

        // Step 2: ERP Central Sales Order Creation & Traceability
        var salesOrder = salesOrderService.createSalesOrder(
                orderUuid,
                orderRef,
                87500.50,
                List.of(
                        new SalesOrderCreatedEvent.ItemDto("TABLE-OAK", "Custom Oak Dining Table", 1, 75000.50),
                        new SalesOrderCreatedEvent.ItemDto("CHAIR-OFFICE", "Ergo Office Chair", 1, 12500.00)
                )
        );
        assertThat(salesOrder.getId()).isEqualTo(orderUuid);
        assertThat(salesOrder.getReferenceCode()).isEqualTo(orderRef);
        assertThat(salesOrder.getItems()).hasSize(2);

        // Step 3: Accounting Ledger Verification
        var ledgerEntry = ledgerService.createLedger(
                orderUuid,
                orderRef,
                "REVENUE-B2C",
                "CREDIT",
                87500.50,
                "B2C Revenue for " + orderRef + " (Custom Oak Dining Table x1, Ergo Office Chair x1)"
        );
        assertThat(ledgerEntry.getId()).isEqualTo(orderUuid);
        assertThat(ledgerEntry.getReferenceCode()).isEqualTo(orderRef);
        assertThat(ledgerEntry.getAmount()).isEqualTo(87500.50);
        assertThat(ledgerEntry.getEntryType()).isEqualTo("CREDIT");

        // Step 4: MES Factory Floor Production Order Planning with Sales Context
        ProductionOrder tableProd = mesService.planProduction("TABLE-OAK", 1, orderUuidStr, orderRef);
        ProductionOrder chairProd = mesService.planProduction("CHAIR-OFFICE", 1, orderUuidStr, orderRef);

        assertThat(tableProd.getSalesOrderId()).isEqualTo(orderUuidStr);
        assertThat(tableProd.getOrderReference()).isEqualTo(orderRef);
        assertThat(tableProd.getStatus()).isEqualTo(ProductionStatus.PLANNED);

        assertThat(chairProd.getSalesOrderId()).isEqualTo(orderUuidStr);
        assertThat(chairProd.getOrderReference()).isEqualTo(orderRef);
        assertThat(chairProd.getStatus()).isEqualTo(ProductionStatus.PLANNED);

        // Step 5: Verify MES Search Filtering Logic by Order UUID, Reference Code, and SKU
        List<ProductionOrder> allMesOrders = mesService.getAllOrders();

        // 5a. Search by Order UUID (e.g. da01c461-...)
        List<ProductionOrder> searchByUuid = allMesOrders.stream()
                .filter(po -> (po.getSalesOrderId() != null && po.getSalesOrderId().contains(orderUuidStr))
                           || po.getId().toString().contains(orderUuidStr))
                .toList();
        assertThat(searchByUuid).hasSizeGreaterThanOrEqualTo(2);
        assertThat(searchByUuid).extracting(ProductionOrder::getSalesOrderId).contains(orderUuidStr);

        // 5b. Search by Reference Code (e.g. ORD-B2C-916370 or 916370)
        List<ProductionOrder> searchByRef = allMesOrders.stream()
                .filter(po -> po.getOrderReference() != null && po.getOrderReference().contains("916370"))
                .toList();
        assertThat(searchByRef).hasSizeGreaterThanOrEqualTo(2);
        assertThat(searchByRef).extracting(ProductionOrder::getOrderReference).contains(orderRef);

        // 5c. Search by SKU (e.g. TABLE-OAK)
        List<ProductionOrder> searchBySku = allMesOrders.stream()
                .filter(po -> po.getProductSku() != null && po.getProductSku().equalsIgnoreCase("TABLE-OAK"))
                .toList();
        assertThat(searchBySku).isNotEmpty();

        // Step 6: MES Production Execution - Routing & Work Orders
        mesService.startProductionOrder(tableProd.getId());
        var startedTable = mesService.getOrder(tableProd.getId());
        assertThat(startedTable.getStatus()).isEqualTo(ProductionStatus.IN_PROGRESS);

        for (WorkOrder wo : startedTable.getWorkOrders()) {
            mesService.startWorkOrder(tableProd.getId(), wo.getId());
            mesService.reportWorkOrderProgress(tableProd.getId(), wo.getId(), 1, 0);
            mesService.completeWorkOrder(tableProd.getId(), wo.getId());
        }

        mesService.completeProductionOrder(tableProd.getId());
        var completedTable = mesService.getOrder(tableProd.getId());
        assertThat(completedTable.getStatus()).isEqualTo(ProductionStatus.COMPLETED);

        // Step 7: Inventory Stock Verification after MES Completion
        inventoryService.createStockItem("TABLE-OAK-FIN", "Finished Oak Dining Table", "WH-BAY-1");
        inventoryService.addStock("TABLE-OAK-FIN", 1);
        var stock = inventoryService.getStock("TABLE-OAK-FIN");
        assertThat(stock.getAvailableQuantity()).isGreaterThanOrEqualTo(1);

        // Step 8: TMS Route Scheduling and Delivery Completion
        var route = tmsService.createRouteWithDetails(
                "ROUTE-ORD-916370",
                "DRV-102 (Express Fleet)",
                "742 Evergreen Terrace, Springfield",
                orderUuidStr
        );
        assertThat(route.getStatus()).isEqualTo("SCHEDULED");
        assertThat(route.getItems()).isNotEmpty();

        tmsService.startRoute(route.getId());
        var startedRoute = tmsService.getById(route.getId());
        assertThat(startedRoute.getStatus()).isEqualTo("IN_TRANSIT");

        UUID stopId = startedRoute.getItems().get(0).getId();
        tmsService.completeStop(startedRoute.getId(), stopId);
        var deliveredRoute = tmsService.getById(route.getId());
        assertThat(deliveredRoute.getStatus()).isEqualTo("COMPLETED");
    }
}
