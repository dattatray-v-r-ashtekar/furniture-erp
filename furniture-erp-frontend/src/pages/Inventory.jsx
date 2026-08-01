import React, { useState } from 'react';
import { Package, Search, Box } from 'lucide-react';

export default function Inventory() {
  const [skuCode, setSkuCode] = useState('SOFA-LEATHER');
  const [stockData, setStockData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchStock = async () => {
    if (!skuCode) return;
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/inventory/items/${skuCode}`);
      if (!response.ok) throw new Error('SKU not found');
      const data = await response.json();
      setStockData(data);
    } catch (err) {
      setError('Failed to fetch stock item. Ensure the SKU is correct.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', gap: '16px', alignItems: 'center' }}>
        <div style={{ flex: 1, display: 'flex', gap: '8px' }}>
          <input 
            type="text" 
            className="form-input" 
            style={{ flex: 1 }} 
            placeholder="Enter Product SKU (e.g. SOFA-LEATHER)..." 
            value={skuCode}
            onChange={(e) => setSkuCode(e.target.value)}
          />
          <button className="btn btn-primary" onClick={fetchStock}>
            <Search size={18} /> Lookup
          </button>
        </div>
      </div>

      {loading && <div style={{ padding: '24px', textAlign: 'center' }}>Loading...</div>}
      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      {stockData && (
        <div className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
            <div>
              <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
                <Package /> {stockData.skuCode}
              </h2>
              <p style={{ color: 'var(--text-secondary)', marginTop: '8px' }}>{stockData.description}</p>
            </div>
            <div>
              <span className={`badge ${stockData.quantity > 0 ? 'badge-success' : 'badge-error'}`} style={{ fontSize: '1rem', padding: '8px 16px' }}>
                {stockData.quantity > 0 ? 'IN STOCK' : 'OUT OF STOCK'}
              </span>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Current Quantity</span>
              <span style={{ fontSize: '3rem', fontWeight: 'bold' }}>{stockData.quantity}</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Location / Bin</span>
              <span style={{ fontSize: '1.5rem', fontWeight: 'bold', display: 'flex', alignItems: 'center', gap: '8px' }}>
                <Box color="var(--accent-primary)" /> {stockData.locationBin}
              </span>
            </div>
          </div>
          
        </div>
      )}
    </div>
  );
}
