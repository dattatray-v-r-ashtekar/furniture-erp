import React, { useState, useEffect } from 'react';
import { Factory, Play, CheckCircle, Clock, RefreshCw, Copy, Check, Link as LinkIcon } from 'lucide-react';

export default function Manufacturing() {
  const [orders, setOrders] = useState([]);
  const [loading, setLoading] = useState(false);
  const [actionLoading, setActionLoading] = useState(null);
  const [error, setError] = useState(null);
  const [successMsg, setSuccessMsg] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');
  const [copiedId, setCopiedId] = useState(null);

  const fetchOrders = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/mes/orders`);
      if (!response.ok) throw new Error('Failed to fetch MES orders');
      const data = await response.json();
      setOrders(data);
    } catch (err) {
      setError('Failed to fetch MES production orders. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchOrders();
  }, []);

  const handleCopy = (text) => {
    navigator.clipboard.writeText(text);
    setCopiedId(text);
    setTimeout(() => setCopiedId(null), 2000);
  };

  const handleStartProduction = async (orderId) => {
    setActionLoading(orderId);
    setSuccessMsg(null);
    try {
      const res = await fetch(`http://localhost:8081/api/v1/mes/orders/${orderId}/start`, { method: 'POST' });
      if (!res.ok) throw new Error('Failed to start production');
      await fetchOrders();
    } catch (err) {
      alert('Error starting production: ' + err.message);
    } finally {
      setActionLoading(null);
    }
  };

  const handleCompleteWorkOrder = async (orderId, workOrderId) => {
    setActionLoading(workOrderId);
    setSuccessMsg(null);
    try {
      const res = await fetch(`http://localhost:8081/api/v1/mes/orders/${orderId}/work-orders/${workOrderId}/complete`, { method: 'POST' });
      if (!res.ok) throw new Error('Failed to complete work order');
      setSuccessMsg('Operation step completed!');
      await fetchOrders();
    } catch (err) {
      alert('Error completing operation step: ' + err.message);
    } finally {
      setActionLoading(null);
    }
  };

  const handleCompleteProduction = async (orderId, sku) => {
    setActionLoading(orderId);
    setSuccessMsg(null);
    try {
      const res = await fetch(`http://localhost:8081/api/v1/mes/orders/${orderId}/complete`, { method: 'POST' });
      if (!res.ok) throw new Error('Failed to complete production');
      setSuccessMsg(`✓ Production for ${sku || 'order'} completed! Inventory stock & TMS delivery route updated.`);
      await fetchOrders();
    } catch (err) {
      alert('Error completing production: ' + err.message);
    } finally {
      setActionLoading(null);
    }
  };

  const filteredOrders = orders.filter(order => {
    if (!searchTerm || !searchTerm.trim()) return true;
    const term = searchTerm.trim().toLowerCase();
    return (
      (order.id && order.id.toLowerCase().includes(term)) ||
      (order.salesOrderId && order.salesOrderId.toLowerCase().includes(term)) ||
      (order.orderReference && order.orderReference.toLowerCase().includes(term)) ||
      (order.productSku && order.productSku.toLowerCase().includes(term)) ||
      (order.status && order.status.toLowerCase().includes(term)) ||
      (order.workOrders && order.workOrders.some(wo => 
        (wo.id && wo.id.toLowerCase().includes(term)) ||
        (wo.operationName && wo.operationName.toLowerCase().includes(term)) ||
        (wo.assignedMachineId && wo.assignedMachineId.toLowerCase().includes(term)) ||
        (wo.status && wo.status.toLowerCase().includes(term))
      ))
    );
  });

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Factory /> Production Orders (MES)
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '4px', fontSize: '0.9rem' }}>
            Manufacturing execution scheduled automatically per purchased item SKU.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <input 
            type="text" 
            placeholder="Search by Order UUID, Sales Order ID, SKU..." 
            value={searchTerm} 
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ minWidth: '320px' }}
          />
          {searchTerm && (
            <button className="btn btn-secondary" onClick={() => setSearchTerm('')} style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
              Clear
            </button>
          )}
          <button className="btn btn-primary" onClick={fetchOrders} disabled={loading}>
            <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
          </button>
        </div>
      </div>

      {successMsg && (
        <div className="glass-panel animate-fade-in" style={{ padding: '16px 24px', borderLeft: '4px solid var(--status-success)', color: 'var(--status-success)', fontWeight: 'bold' }}>
          {successMsg}
        </div>
      )}

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(400px, 1fr))', gap: '24px' }}>
        {filteredOrders.map(orderData => (
          <div key={orderData.id} className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px', gap: '12px' }}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '4px', maxWidth: '70%' }}>
                <span style={{ fontSize: '0.75rem', color: 'var(--text-secondary)', textTransform: 'uppercase', letterSpacing: '0.5px' }}>Production Order ID</span>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <span style={{ fontFamily: 'monospace', fontSize: '0.85rem', color: 'var(--accent-primary)', wordBreak: 'break-all' }}>{orderData.id}</span>
                  <button 
                    onClick={() => handleCopy(orderData.id)} 
                    style={{ background: 'none', border: 'none', cursor: 'pointer', padding: '2px', color: copiedId === orderData.id ? 'var(--status-success)' : 'var(--text-secondary)' }}
                    title="Copy UUID"
                  >
                    {copiedId === orderData.id ? <Check size={14} /> : <Copy size={14} />}
                  </button>
                </div>

                {(orderData.salesOrderId || orderData.orderReference) && (
                  <div style={{ marginTop: '6px', fontSize: '0.75rem', color: 'var(--text-secondary)', display: 'flex', alignItems: 'center', gap: '6px', flexWrap: 'wrap' }}>
                    <LinkIcon size={12} color="var(--accent-primary)" />
                    <span>Sales Order:</span>
                    <span style={{ fontFamily: 'monospace', color: 'var(--text-primary)' }}>{orderData.salesOrderId || 'N/A'}</span>
                    {orderData.orderReference && (
                      <span className="badge badge-secondary" style={{ fontSize: '0.7rem', padding: '2px 6px' }}>{orderData.orderReference}</span>
                    )}
                  </div>
                )}
              </div>

              <div>
                <span className={`badge ${orderData.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`} style={{ fontSize: '0.85rem', padding: '6px 12px' }}>
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
                {orderData.workOrders?.map((wo, i) => {
                  const isDone = wo.status === 'COMPLETED' || wo.status === 'DONE' || orderData.status === 'COMPLETED';
                  return (
                    <div key={wo.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px', background: 'rgba(0,0,0,0.2)', borderRadius: 'var(--radius-sm)' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                        <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--bg-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.8rem' }}>{i + 1}</div>
                        <div>
                          <div style={{ fontWeight: 'bold', fontSize: '0.9rem' }}>{wo.operationName}</div>
                          <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Machine: {wo.assignedMachineId} | Status: {wo.status}</div>
                        </div>
                      </div>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
                        {isDone ? (
                          <span style={{ display: 'flex', alignItems: 'center', gap: '4px', color: 'var(--status-success)', fontSize: '0.8rem' }}>
                            <CheckCircle size={16} color="var(--status-success)" /> Done
                          </span>
                        ) : (
                          <button 
                            className="btn btn-secondary" 
                            style={{ fontSize: '0.75rem', padding: '4px 8px' }}
                            onClick={() => handleCompleteWorkOrder(orderData.id, wo.id)}
                            disabled={actionLoading === wo.id}
                          >
                            {actionLoading === wo.id ? '...' : 'Complete Step'}
                          </button>
                        )}
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            <div style={{ display: 'flex', gap: '16px', marginTop: 'auto', flexDirection: 'column' }}>
              {orderData.status !== 'COMPLETED' ? (
                <div style={{ display: 'flex', gap: '12px', width: '100%' }}>
                  {orderData.status === 'PLANNED' && (
                    <button 
                      className="btn btn-secondary" 
                      style={{ flex: 1 }} 
                      onClick={() => handleStartProduction(orderData.id)}
                      disabled={actionLoading === orderData.id}
                    >
                      {actionLoading === orderData.id ? 'Starting...' : 'Start Production'}
                    </button>
                  )}
                  <button 
                    className="btn btn-primary" 
                    style={{ flex: 1 }} 
                    onClick={() => handleCompleteProduction(orderData.id, orderData.productSku)}
                    disabled={actionLoading === orderData.id}
                  >
                    {actionLoading === orderData.id ? 'Finalizing...' : 'Complete Production'}
                  </button>
                </div>
              ) : (
                <div style={{ width: '100%', textAlign: 'center', color: 'var(--status-success)', fontWeight: 'bold', padding: '12px', background: 'rgba(16, 185, 129, 0.1)', borderRadius: 'var(--radius-sm)' }}>
                  ✓ Production Complete & Stock Ready
                </div>
              )}
            </div>
          </div>
        ))}
        {filteredOrders.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>
            {searchTerm ? `No production orders matching "${searchTerm}".` : 'No production orders found.'}
          </div>
        )}
      </div>
    </div>
  );
}
