import React, { useState } from 'react';
import { Briefcase, ShoppingCart, CheckCircle } from 'lucide-react';

export default function DealerPortal() {
  const [dealerId, setDealerId] = useState('IKEA-NY');
  const [sku, setSku] = useState('STANDARD-CHAIR');
  const [qty, setQty] = useState(100);
  const [status, setStatus] = useState(null);

  const placeBulkOrder = async () => {
    setStatus('processing');
    try {
      const payload = {
        dealerId,
        bulkDiscountId: 'DISC-20',
        items: [{ sku, qty: parseInt(qty) }]
      };
      
      await fetch('http://localhost:8081/api/v1/dealer/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%', maxWidth: '800px' }}>
      <div className="glass-panel" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Briefcase /> B2B Wholesale Dealer Portal
        </h2>
        <p style={{ color: 'var(--text-secondary)' }}>Place bulk orders for retail partners. (Scenario 3)</p>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Dealer ID</label>
            <input type="text" className="form-input" value={dealerId} onChange={e => setDealerId(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">Product SKU</label>
            <input type="text" className="form-input" value={sku} onChange={e => setSku(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">Quantity</label>
            <input type="number" className="form-input" value={qty} onChange={e => setQty(e.target.value)} />
          </div>
        </div>

        <button 
          className="btn btn-primary" 
          onClick={placeBulkOrder}
          disabled={status === 'processing' || status === 'success'}
        >
          <ShoppingCart size={18} /> Place Bulk Order
        </button>

        {status === 'success' && (
          <div className="animate-fade-in" style={{ padding: '16px', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid var(--status-success)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-success)', fontWeight: 'bold' }}>
              <CheckCircle size={18} /> Bulk Order Placed!
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '8px' }}>
              Event fired to Kafka. This will cascade down to MES to auto-generate a massive WorkOrder. 
              <br/><em>Go to the Manufacturing tab to view the WorkOrder, complete it, and then log a QA failure in the QMS tab.</em>
            </p>
          </div>
        )}
      </div>
    </div>
  );
}
