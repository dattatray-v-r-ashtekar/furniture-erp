import React, { useState, useEffect } from 'react';
import { Briefcase, ShoppingCart, CheckCircle, RefreshCw, FileText } from 'lucide-react';

export default function DealerPortal() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [dealerId, setDealerId] = useState('IKEA-NY');
  const [sku, setSku] = useState('STANDARD-CHAIR');
  const [qty, setQty] = useState(100);
  const [status, setStatus] = useState(null);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/dealer/orders`);
      if (!response.ok) throw new Error('Failed to fetch wholesale orders');
      const data = await response.json();
      setOrders(data);
    } catch (err) {
      setError('Failed to fetch wholesale orders. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const placeBulkOrder = async () => {
    setStatus('processing');
    try {
      const referenceCode = `B2B-${dealerId}-${sku}-${qty}`;
      
      await fetch('http://localhost:8081/api/v1/dealer/orders', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ referenceCode })
      });
      
      setStatus('success');
      fetchOrders();
      setTimeout(() => setStatus('idle'), 3000);
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Briefcase /> B2B Wholesale Dealer Portal
        </h2>
        <button className="btn btn-primary" onClick={fetchOrders} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      <div className="glass-panel" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <ShoppingCart /> Place Bulk Order
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
          <ShoppingCart size={18} /> Submit Bulk Order
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

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '24px' }}>
        {orders.map(order => (
          <div key={order.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '12px' }}>
              <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{order.id}</p>
            </div>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Reference Code</span>
              <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>{order.referenceCode}</span>
            </div>
          </div>
        ))}
        {orders.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No wholesale orders found.</div>
        )}
      </div>
    </div>
  );
}
