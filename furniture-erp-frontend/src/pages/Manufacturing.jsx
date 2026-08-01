import React, { useState, useEffect } from 'react';
import { Search, Factory, CheckCircle, Clock } from 'lucide-react';
import { getRecentId } from '../store';

export default function Manufacturing() {
  const [orderId, setOrderId] = useState('');
  const [orderData, setOrderData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    // If the E-commerce checkout created a sales order, MES might have caught it and we stored the ID (or we can just check recent IDs)
    // Actually, MES auto-generates the UUID, so we won't know it unless we fetch it.
    // Wait, the MES service doesn't return the ID to the storefront. 
    // We will just let the user paste an ID, or simulate a Kanban board by polling known IDs if we had them.
  }, []);

  const fetchOrder = async (idToFetch) => {
    if (!idToFetch) return;
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/mes/work-orders/${idToFetch}`);
      if (!response.ok) throw new Error('Order not found');
      const data = await response.json();
      setOrderData(data);
    } catch (err) {
      setError('Failed to fetch order. Ensure it exists and the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  const handleStartProduction = async () => {
    try {
      await fetch(`http://localhost:8081/api/v1/mes/work-orders/${orderData.id}/start`, { method: 'POST' });
      fetchOrder(orderData.id); // Refresh
    } catch (err) {
      console.error(err);
    }
  };

  const handleCompleteProduction = async () => {
    try {
      await fetch(`http://localhost:8081/api/v1/mes/work-orders/${orderData.id}/complete`, { method: 'POST' });
      fetchOrder(orderData.id); // Refresh
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      {/* Search Bar */}
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', gap: '16px', alignItems: 'center' }}>
        <div style={{ flex: 1, display: 'flex', gap: '8px' }}>
          <input 
            type="text" 
            className="form-input" 
            style={{ flex: 1 }} 
            placeholder="Enter Production Order UUID to view status..." 
            value={orderId}
            onChange={(e) => setOrderId(e.target.value)}
          />
          <button className="btn btn-primary" onClick={() => fetchOrder(orderId)}>
            <Search size={18} /> Lookup
          </button>
        </div>
      </div>

      {/* Results Dashboard */}
      {loading && <div style={{ padding: '24px', textAlign: 'center' }}>Loading...</div>}
      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      {orderData && (
        <div className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
            <div>
              <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
                <Factory /> Production Order
              </h2>
              <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', marginTop: '8px' }}>{orderData.id}</p>
            </div>
            <div>
              <span className={`badge ${orderData.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`} style={{ fontSize: '1rem', padding: '8px 16px' }}>
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
            <h3 style={{ marginBottom: '16px' }}>Assembly Routing</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
              {orderData.workOrders?.map((wo, i) => (
                <div key={wo.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '16px', background: 'rgba(0,0,0,0.2)', borderRadius: 'var(--radius-sm)' }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                    <div style={{ width: '32px', height: '32px', borderRadius: '50%', background: 'var(--bg-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{i + 1}</div>
                    <div>
                      <div style={{ fontWeight: 'bold' }}>{wo.operationName}</div>
                      <div style={{ fontSize: '0.8rem', color: 'var(--text-secondary)' }}>Machine: {wo.assignedMachineId}</div>
                    </div>
                  </div>
                  <div>
                    {wo.status === 'COMPLETED' ? <CheckCircle color="var(--status-success)" /> : <Clock color="var(--status-warning)" />}
                  </div>
                </div>
              ))}
            </div>
          </div>

          {/* Action Buttons */}
          <div style={{ display: 'flex', gap: '16px', marginTop: '16px' }}>
            {orderData.status === 'PLANNED' && (
              <button className="btn btn-primary" onClick={handleStartProduction}>Start Production</button>
            )}
            {orderData.status === 'IN_PROGRESS' && (
              <button className="btn btn-primary" onClick={handleCompleteProduction}>Complete Production</button>
            )}
          </div>
          
        </div>
      )}
    </div>
  );
}
