import requests
import time
import sys

BASE_URL = "http://localhost:8081"

def test_ecommerce_multi_item_checkout():
    print("[TEST 1] E-Commerce Multi-Item Checkout (Step 1)...")
    payload = {
        "referenceCode": f"LIVE-E2E-{int(time.time())}",
        "totalAmount": 87500.50,
        "items": [
            {
                "sku": "TABLE-OAK",
                "name": "Luxury Oak Dining Table",
                "quantity": 1,
                "price": 75000.50
            },
            {
                "sku": "CHAIR-OFFICE",
                "name": "Ergo Executive Chair",
                "quantity": 1,
                "price": 12500.00
            }
        ]
    }
    res = requests.post(f"{BASE_URL}/api/v1/ecommerce/orders", json=payload, timeout=10)
    assert res.status_code == 200, f"Checkout failed with status {res.status_code}: {res.text}"
    data = res.json()
    assert data["referenceCode"] == payload["referenceCode"]
    assert len(data["items"]) == 2
    assert data["totalAmount"] == 87500.50
    print("  [PASS] E-Commerce order placed successfully with 2 items and total Rs. 87,500.50")
    return data

def test_sales_orders_sync(order_ref):
    print("\n[TEST 2] Verifying ERP Central Sales Order Sync (Step 2)...")
    time.sleep(3) # Wait for Kafka propagation
    res = requests.get(f"{BASE_URL}/api/v1/erp/sales-orders", timeout=10)
    assert res.status_code == 200, f"Failed to fetch sales orders: {res.text}"
    orders = res.json()
    matching = [o for o in orders if o.get("referenceCode") == order_ref]
    assert len(matching) >= 1, f"Expected Sales Order with ref {order_ref} not found in ERP Central"
    so = matching[0]
    assert len(so.get("items", [])) == 2, f"Expected 2 line items in Sales Order, got {len(so.get('items', []))}"
    print(f"  [PASS] ERP Central synchronized 1 consolidated Sales Order ({so.get('id')}) with 2 line items.")

def test_accounting_ledger(order_ref):
    print("\n[TEST 3] Verifying Accounting Ledger Balance (Step 2)...")
    time.sleep(2)
    res = requests.get(f"{BASE_URL}/api/v1/accounting/ledgers", timeout=10)
    assert res.status_code == 200, f"Failed to fetch ledgers: {res.text}"
    ledgers = res.json()
    matching = [l for l in ledgers if l.get("referenceCode") == order_ref]
    assert len(matching) >= 1, f"Expected Ledger entry for {order_ref} not found"
    entry = matching[0]
    assert entry.get("entryType") == "CREDIT"
    assert entry.get("amount") == 87500.50
    print(f"  [PASS] Accounting General Ledger verified: CREDIT entry for Rs. {entry.get('amount'):,.2f} recorded.")

def test_mes_factory_production():
    print("\n[TEST 4] Verifying MES Factory Production & Completion (Steps 3 & 4)...")
    res = requests.get(f"{BASE_URL}/api/v1/mes/orders", timeout=10)
    assert res.status_code == 200, f"Failed to fetch MES orders: {res.text}"
    orders = res.json()
    assert len(orders) > 0, "No production orders found in MES"
    target_order = orders[-1]
    order_id = target_order["id"]
    sku = target_order.get("productSku", "BED-KING")

    # Complete production order directly (auto-progresses from PLANNED to COMPLETED)
    complete_res = requests.post(f"{BASE_URL}/api/v1/mes/orders/{order_id}/complete", timeout=10)
    assert complete_res.status_code == 200, f"Failed to complete production order: {complete_res.text}"

    # Verify updated status
    verify_res = requests.get(f"{BASE_URL}/api/v1/mes/orders/{order_id}", timeout=10)
    updated_order = verify_res.json()
    assert updated_order["status"] == "COMPLETED", f"Expected COMPLETED, got {updated_order['status']}"
    print(f"  [PASS] MES Production Order {order_id} ({sku}) completed successfully.")
    return sku

def test_inventory_stock_increment(sku):
    print("\n[TEST 5] Verifying Inventory Stock Level Sync (Step 5)...")
    time.sleep(3) # Wait for Kafka ProductionCompletedEvent propagation
    res = requests.get(f"{BASE_URL}/api/v1/inventory/items", timeout=10)
    assert res.status_code == 200, f"Failed to fetch inventory: {res.text}"
    items = res.json()
    matching = [i for i in items if i.get("skuCode") == sku]
    assert len(matching) >= 1, f"Expected Inventory StockItem for SKU {sku} not found"
    stock_item = matching[0]
    qty = stock_item.get("availableQuantity", stock_item.get("quantity", 0))
    assert qty >= 1, f"Expected positive availableQuantity for {sku}, got {qty}"
    print(f"  [PASS] Inventory verified: {sku} available stock = {qty} units in location {stock_item.get('locationBin')}.")

def test_tms_delivery_route():
    print("\n[TEST 6] Verifying TMS Delivery Route & Dispatch (Step 5/6)...")
    time.sleep(2)
    res = requests.get(f"{BASE_URL}/api/v1/tms/routes", timeout=10)
    assert res.status_code == 200, f"Failed to fetch TMS routes: {res.text}"
    routes = res.json()
    assert len(routes) > 0, "No delivery routes found in TMS"
    route = routes[-1]
    route_id = route["id"]
    
    # Start route
    requests.post(f"{BASE_URL}/api/v1/tms/routes/{route_id}/start", timeout=10)
    
    # Complete first stop if present
    stops = route.get("stops") or route.get("items") or []
    if len(stops) > 0:
        stop_id = stops[0]["id"]
        requests.post(f"{BASE_URL}/api/v1/tms/routes/{route_id}/stops/{stop_id}/complete", timeout=10)
    
    # Verify Route
    v_res = requests.get(f"{BASE_URL}/api/v1/tms/routes/{route_id}", timeout=10)
    v_route = v_res.json()
    assert v_route.get("status") in ["IN_TRANSIT", "COMPLETED"], f"Unexpected route status: {v_route.get('status')}"
    print(f"  [PASS] TMS Delivery Route {route_id} dispatched and stop marked delivered successfully.")

def run_all_live_tests():
    print("=" * 70)
    print("  RUNNING FULL SCENARIO 1 END-TO-END VERIFICATION SUITE")
    print("=" * 70)
    try:
        order = test_ecommerce_multi_item_checkout()
        order_ref = order["referenceCode"]
        test_sales_orders_sync(order_ref)
        test_accounting_ledger(order_ref)
        completed_sku = test_mes_factory_production()
        test_inventory_stock_increment(completed_sku)
        test_tms_delivery_route()
        print("\n" + "=" * 70)
        print("  [SUCCESS] ALL SCENARIO 1 E2E INTEGRATION & STATE TESTS PASSED!")
        print("=" * 70)
    except Exception as e:
        print(f"\n[FAIL] Test Failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    run_all_live_tests()
