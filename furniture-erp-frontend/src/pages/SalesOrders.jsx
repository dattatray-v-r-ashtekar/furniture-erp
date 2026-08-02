import React, { useState, useEffect } from 'react';
import { ShoppingBag, RefreshCw, CheckCircle2, Clock } from 'lucide-react';

export default function SalesOrders() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/erp/sales-orders`);
      if (!response.ok) throw new Error('Failed to fetch sales orders');
      const data = await response.json();
      setOrders(data);
    } catch (err) {
      setError('Failed to fetch sales orders. Ensure the backend is running on port 8081.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const filteredOrders = orders.filter(order => 
    order.id?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    order.referenceCode?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    order.status?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      {/* Header & Filter Controls */}
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <ShoppingBag /> ERP Central - Sales Orders
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '4px', fontSize: '0.9rem' }}>
            Sales orders generated automatically by Kafka events from E-commerce and B2B Dealer Portal.
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <input 
            type="text" 
            placeholder="Search by Order ID / Ref Code..." 
            value={searchTerm} 
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ minWidth: '280px' }}
          />
          <button className="btn btn-primary" onClick={fetchOrders} disabled={loading}>
            <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
          </button>
        </div>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      {/* Sales Orders Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(340px, 1fr))', gap: '20px' }}>
        {filteredOrders.map(order => (
          <div key={order.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid var(--border-glass)', paddingBottom: '12px' }}>
              <div>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', textTransform: 'uppercase' }}>Order ID</span>
                <p style={{ color: 'var(--accent-primary)', fontFamily: 'monospace', fontSize: '0.85rem', fontWeight: 'bold' }}>
                  {order.id}
                </p>
              </div>
              <span className={`badge ${order.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`}>
                {order.status || 'CONFIRMED'}
              </span>
            </div>

            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <div>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>Reference Code:</span>
                <p style={{ fontWeight: 600, marginTop: '2px', fontSize: '0.95rem' }}>{order.referenceCode || 'N/A'}</p>
              </div>
              <div style={{ textAlign: 'right' }}>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>Total:</span>
                <p style={{ fontWeight: 'bold', color: 'var(--status-success)', fontSize: '1.1rem' }}>
                  ₹{Number(order.totalAmount || 0).toFixed(2)}
                </p>
              </div>
            </div>

            {(order.items || order.lines) && (order.items || order.lines).length > 0 && (
              <div>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>Order Line Items:</span>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '6px', marginTop: '6px' }}>
                  {(order.items || order.lines).map((line, idx) => (
                    <div key={idx} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', background: 'rgba(0,0,0,0.2)', padding: '8px 12px', borderRadius: '4px', fontSize: '0.85rem' }}>
                      <div>
                        <strong>{line.sku || line.productId || 'ITEM'}</strong>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>{line.description || line.name}</div>
                      </div>
                      <div style={{ textAlign: 'right' }}>
                        <span>Qty: {line.quantity || 1}</span>
                        {line.price ? <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>₹{Number(line.price).toFixed(2)}</div> : null}
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}

            <div style={{ marginTop: 'auto', paddingTop: '12px', borderTop: '1px solid var(--border-glass)', display: 'flex', justifyContent: 'space-between', alignItems: 'center', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
              <span style={{ display: 'flex', alignItems: 'center', gap: '4px' }}>
                <CheckCircle2 size={14} color="var(--status-success)" /> Synchronized via Kafka
              </span>
            </div>
          </div>
        ))}

        {filteredOrders.length === 0 && !loading && (
          <div className="glass-panel" style={{ padding: '32px', textAlign: 'center', gridColumn: '1 / -1', color: 'var(--text-secondary)' }}>
            No sales orders found {searchTerm ? `matching "${searchTerm}"` : ''}.
          </div>
        )}
      </div>

    </div>
  );
}
