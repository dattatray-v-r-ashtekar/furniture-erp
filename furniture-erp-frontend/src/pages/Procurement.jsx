import React, { useState, useEffect } from 'react';
import { Calculator, Send, CheckCircle, RefreshCw, FileText } from 'lucide-react';

export default function Procurement() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [vendorId, setVendorId] = useState('LUMBER-INC');
  const [sku, setSku] = useState('RAW-WOOD-001');
  const [qty, setQty] = useState(5000);
  const [status, setStatus] = useState(null); // idle, processing, issued, error

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/procurement/orders`);
      if (!response.ok) throw new Error('Failed to fetch purchase orders');
      const data = await response.json();
      setOrders(data);
    } catch (err) {
      setError('Failed to fetch purchase orders. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const issuePO = async () => {
    setStatus('processing');
    try {
      // Step 1: Create draft order
      const createRes = await fetch('http://localhost:8081/api/v1/procurement/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ vendorId })
      });
      const order = await createRes.json();
      
      // Step 2: Add line item
      await fetch(`http://localhost:8081/api/v1/procurement/orders/${order.id}/lines`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ skuCode: sku, quantity: parseInt(qty), unitPrice: 1.5 })
      });

      // Step 3: Issue order
      await fetch(`http://localhost:8081/api/v1/procurement/orders/${order.id}/issue`, {
        method: 'POST'
      });
      
      setStatus('issued');
      fetchOrders();
      
      setTimeout(() => setStatus('idle'), 3000);
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const receiveGoods = async (orderId) => {
    try {
      await fetch(`http://localhost:8081/api/v1/procurement/orders/${orderId}/receive`, {
        method: 'POST'
      });
      fetchOrders();
    } catch (error) {
      console.error(error);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <FileText /> Procurement
        </h2>
        <button className="btn btn-primary" onClick={fetchOrders} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      <div className="glass-panel" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Calculator /> Issue New Purchase Order
        </h2>
        <p style={{ color: 'var(--text-secondary)' }}>Order raw materials from vendors. (Scenario 2)</p>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Vendor ID</label>
            <input type="text" className="form-input" value={vendorId} onChange={e => setVendorId(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">Raw Material SKU</label>
            <input type="text" className="form-input" value={sku} onChange={e => setSku(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">Quantity</label>
            <input type="number" className="form-input" value={qty} onChange={e => setQty(e.target.value)} />
          </div>
        </div>

        <button 
          className="btn btn-primary" 
          onClick={issuePO}
          disabled={status === 'processing'}
        >
          <Send size={18} /> Submit Purchase Order
        </button>

        {status === 'issued' && (
          <div className="animate-fade-in" style={{ padding: '16px', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid var(--status-success)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-success)', fontWeight: 'bold' }}>
              <CheckCircle size={18} /> PO Issued Successfully!
            </div>
          </div>
        )}
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(400px, 1fr))', gap: '24px' }}>
        {orders.map(order => (
          <div key={order.id} className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
              <div>
                <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{order.id}</p>
                <h3 style={{ marginTop: '8px', fontSize: '1.2rem' }}>Vendor: {order.vendorId}</h3>
              </div>
              <div>
                <span className={`badge ${order.status === 'RECEIVED' ? 'badge-success' : 'badge-warning'}`}>
                  {order.status}
                </span>
              </div>
            </div>

            <div>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Lines:</span>
              {order.lines.map((line, i) => (
                <div key={i} style={{ display: 'flex', justifyContent: 'space-between', padding: '8px 0', borderBottom: '1px solid rgba(255,255,255,0.05)' }}>
                  <span>{line.skuCode}</span>
                  <span style={{ fontWeight: 'bold' }}>{line.quantity} @ ₹{line.unitPrice}</span>
                </div>
              ))}
            </div>

            <div style={{ marginTop: 'auto' }}>
              {order.status === 'ISSUED' && (
                <button className="btn btn-secondary" style={{ width: '100%', marginTop: '16px' }} onClick={() => receiveGoods(order.id)}>
                  <CheckCircle size={18} /> Simulate Truck Arrival (Receive)
                </button>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
