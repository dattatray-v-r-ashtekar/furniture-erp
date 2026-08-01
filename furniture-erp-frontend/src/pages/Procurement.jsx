import React, { useState } from 'react';
import { Calculator, Send, CheckCircle } from 'lucide-react';

export default function Procurement() {
  const [vendorId, setVendorId] = useState('LUMBER-INC');
  const [sku, setSku] = useState('RAW-WOOD-001');
  const [qty, setQty] = useState(5000);
  const [status, setStatus] = useState(null);
  const [log, setLog] = useState(null);
  const [orderId, setOrderId] = useState(null);

  const issuePO = async () => {
    setStatus('processing');
    try {
      const payload = {
        vendorId,
        items: [{ sku, qty: parseInt(qty) }],
        totalCost: qty * 1.5 // Mock pricing
      };
      
      const response = await fetch('http://localhost:8081/api/v1/procurement/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      const data = await response.json();
      setLog(data);
      setOrderId(data.id || data.orderId || (data.split && data.split(':')[1]) || 'PO-SUCCESS');
      setStatus('issued');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const receiveGoods = async () => {
    setStatus('processing_receipt');
    try {
      await fetch(`http://localhost:8081/api/v1/procurement/orders/${orderId}/receive`, {
        method: 'POST'
      });
      setStatus('received');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%', maxWidth: '800px' }}>
      <div className="glass-panel" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Calculator /> Issue Purchase Order
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
          disabled={status === 'processing' || status === 'issued' || status === 'received'}
        >
          <Send size={18} /> Submit Purchase Order
        </button>

        {status === 'issued' && (
          <div className="animate-fade-in" style={{ padding: '16px', background: 'rgba(59, 130, 246, 0.1)', border: '1px solid var(--status-info)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-info)', marginBottom: '16px', fontWeight: 'bold' }}>
              <CheckCircle size={18} /> PO Issued! Waiting for Vendor Delivery...
            </div>
            
            <button className="btn btn-glass" onClick={receiveGoods} style={{ width: '100%' }}>
              Simulate Truck Arrival & Receive Goods
            </button>
          </div>
        )}

        {status === 'received' && (
          <div className="animate-fade-in" style={{ padding: '16px', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid var(--status-success)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-success)', fontWeight: 'bold' }}>
              <CheckCircle size={18} /> Goods Received!
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '8px' }}>
              <strong>GoodsReceivedEvent</strong> fired to Kafka. WMS will now assign physical shelf space, and Accounting will log an Accounts Payable entry.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
