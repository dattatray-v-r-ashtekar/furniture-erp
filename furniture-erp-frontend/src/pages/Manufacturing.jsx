import React, { useState, useEffect } from 'react';
import { Factory, CheckCircle, Clock, RefreshCw } from 'lucide-react';

export default function Manufacturing() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/mes/orders`);
      if (!response.ok) throw new Error('Failed to fetch orders');
      const data = await response.json();
      setOrders(data);
    } catch (err) {
      setError('Failed to fetch production orders. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleStartProduction = async (orderId) => {
    try {
      await fetch(`http://localhost:8081/api/v1/mes/orders/${orderId}/start`, { method: 'POST' });
      fetchOrders();
    } catch (err) {
      console.error(err);
    }
  };

  const handleCompleteProduction = async (orderId) => {
    try {
      await fetch(`http://localhost:8081/api/v1/mes/orders/${orderId}/complete`, { method: 'POST' });
      fetchOrders();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Factory /> Production Orders
        </h2>
        <button className="btn btn-primary" onClick={fetchOrders} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(400px, 1fr))', gap: '24px' }}>
        {orders.map(orderData => (
          <div key={orderData.id} className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
              <div>
                <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{orderData.id}</p>
              </div>
              <div>
                <span className={`badge ${orderData.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`} style={{ fontSize: '0.9rem', padding: '6px 12px' }}>
                  {orderData.status}
                </span>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Product SKU</span>
                <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>{orderData.productSku}</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Target Quantity</span>
                <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>{orderData.targetQuantity} Units</span>
              </div>
            </div>

            <div>
              <h3 style={{ marginBottom: '16px', fontSize: '1rem' }}>Assembly Routing</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {orderData.workOrders?.map((wo, i) => (
                  <div key={wo.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px', background: 'rgba(0,0,0,0.2)', borderRadius: 'var(--radius-sm)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                      <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--bg-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.8rem' }}>{i + 1}</div>
                      <div>
                        <div style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>{wo.operationName}</div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Machine: {wo.assignedMachineId}</div>
                      </div>
                    </div>
                    <div>
                      {wo.status === 'COMPLETED' ? <CheckCircle size={16} color="var(--status-success)" /> : <Clock size={16} color="var(--status-warning)" />}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div style={{ display: 'flex', gap: '16px', marginTop: 'auto' }}>
              {orderData.status === 'PLANNED' && (
                <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => handleStartProduction(orderData.id)}>Start Production</button>
              )}
              {orderData.status === 'IN_PROGRESS' && (
                <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => handleCompleteProduction(orderData.id)}>Complete Production</button>
              )}
            </div>
          </div>
        ))}
        {orders.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No production orders found.</div>
        )}
      </div>
    </div>
  );
}
