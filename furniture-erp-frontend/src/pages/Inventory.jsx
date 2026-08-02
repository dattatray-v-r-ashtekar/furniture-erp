import React, { useState, useEffect } from 'react';
import { Package, Box, RefreshCw } from 'lucide-react';

export default function Inventory() {
  const [stockItems, setStockItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchStock = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/inventory/items`);
      if (!response.ok) throw new Error('Failed to fetch stock items');
      const data = await response.json();
      setStockItems(data);
    } catch (err) {
      setError('Failed to fetch stock items. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStock();
  }, []);

  const filteredStock = stockItems.filter(item =>
    item.skuCode?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    item.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    item.locationBin?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Package /> Inventory (Finished Goods & Materials)
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '4px', fontSize: '0.9rem' }}>
            Real-time stock ledger synchronized with production completions and vendor receipts.
          </p>
        </div>
        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <input 
            type="text" 
            placeholder="Search SKU or description..." 
            value={searchTerm} 
            onChange={(e) => setSearchTerm(e.target.value)}
            style={{ minWidth: '240px' }}
          />
          <button className="btn btn-primary" onClick={fetchStock} disabled={loading}>
            <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
          </button>
        </div>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '16px' }}>
        {filteredStock.map(stockData => {
          const qty = stockData.availableQuantity ?? stockData.quantity ?? 0;
          return (
            <div key={stockData.skuCode} className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
                <div>
                  <h3 style={{ fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    {stockData.skuCode}
                  </h3>
                  <p style={{ color: 'var(--text-secondary)', marginTop: '4px', fontSize: '0.9rem' }}>{stockData.description}</p>
                </div>
                <div>
                  <span className={`badge ${qty > 0 ? 'badge-success' : 'badge-error'}`} style={{ fontSize: '0.8rem', padding: '6px 12px' }}>
                    {qty > 0 ? 'IN STOCK' : 'OUT OF STOCK'}
                  </span>
                </div>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '16px' }}>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>Available Quantity</span>
                  <span style={{ fontSize: '2rem', fontWeight: 'bold' }}>{qty}</span>
                </div>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
                  <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem' }}>Location Bin</span>
                  <span style={{ fontSize: '1.2rem', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '8px' }}>
                    <Box size={18} color="var(--accent-primary)" /> {stockData.locationBin || 'N/A'}
                  </span>
                </div>
              </div>
            </div>
          );
        })}
        {filteredStock.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No stock items found.</div>
        )}
      </div>
    </div>
  );
}
