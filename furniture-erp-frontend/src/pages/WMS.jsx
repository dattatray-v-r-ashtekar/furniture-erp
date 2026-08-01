import React, { useState, useEffect } from 'react';
import { Warehouse, Search } from 'lucide-react';

export default function WMS() {
  const [bins, setBins] = useState([]);
  const [loading, setLoading] = useState(false);

  const fetchBins = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/wms/bins`);
      if (response.ok) {
        const data = await response.json();
        setBins(data);
      }
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchBins();
  }, []);

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}><Warehouse /> Warehouse Floor Bins</h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '8px' }}>View physically allocated space for raw materials.</p>
        </div>
        <button className="btn btn-primary" onClick={fetchBins}><Search size={18} /> Refresh</button>
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '16px' }}>
        {loading && <div>Loading bins...</div>}
        {!loading && bins.length === 0 && <div>No bins allocated yet. Receive goods in Procurement first!</div>}
        
        {bins.map((bin) => (
          <div key={bin.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>Aisle {bin.aisle} - Rack {bin.rack}</span>
              <Warehouse color="var(--accent-primary)" />
            </div>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Allocated SKU</span>
              <span style={{ fontSize: '1.1rem', fontWeight: '600', color: 'var(--status-info)' }}>{bin.allocatedSku || 'EMPTY'}</span>
            </div>
            
            <div style={{ background: 'rgba(255,255,255,0.05)', height: '8px', borderRadius: '4px', overflow: 'hidden' }}>
              <div style={{ 
                width: `${(bin.currentLoad / bin.maxCapacity) * 100}%`, 
                background: 'linear-gradient(90deg, var(--accent-primary), var(--accent-secondary))', 
                height: '100%' 
              }} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: '0.8rem', color: 'var(--text-secondary)' }}>
              <span>{bin.currentLoad} Load</span>
              <span>{bin.maxCapacity} Max Capacity</span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
