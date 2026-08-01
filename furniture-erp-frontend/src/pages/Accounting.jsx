import React, { useState, useEffect } from 'react';
import { Calculator, RefreshCw, Search } from 'lucide-react';

export default function Accounting() {
  const [ledgers, setLedgers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const [searchTerm, setSearchTerm] = useState('');

  const fetchLedgers = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/accounting/ledgers`);
      if (!response.ok) throw new Error('Failed to fetch ledgers');
      const data = await response.json();
      setLedgers(data);
    } catch (err) {
      setError('Failed to fetch ledgers. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchLedgers();
  }, []);

  const filteredLedgers = ledgers.filter(ledger =>
    ledger.id?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    ledger.accountId?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    ledger.description?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    ledger.entryType?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      {/* Top Bar with Search & Refresh */}
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center', flexWrap: 'wrap', gap: '16px' }}>
        <div>
          <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
            <Calculator /> General Ledger
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '4px', fontSize: '0.9rem' }}>
            Real-time accounting entries generated automatically by Kafka events (Revenue, AP, Payroll).
          </p>
        </div>

        <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
          <div style={{ position: 'relative' }}>
            <input 
              type="text" 
              placeholder="Search / Enter Ledger UUID or Account..." 
              value={searchTerm} 
              onChange={(e) => setSearchTerm(e.target.value)}
              style={{ minWidth: '320px', paddingRight: '36px' }}
            />
            {searchTerm && (
              <button 
                onClick={() => setSearchTerm('')} 
                style={{ position: 'absolute', right: '10px', top: '50%', transform: 'translateY(-50%)', background: 'transparent', border: 'none', color: 'var(--text-secondary)', cursor: 'pointer' }}
              >
                ✕
              </button>
            )}
          </div>
          <button className="btn btn-primary" onClick={fetchLedgers} disabled={loading}>
            <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
          </button>
        </div>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '16px' }}>
        {filteredLedgers.map(ledger => (
          <div key={ledger.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '12px' }}>
              <div>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', textTransform: 'uppercase' }}>Ledger UUID</span>
                <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem', wordBreak: 'break-all' }}>{ledger.id}</p>
              </div>
              <span className={`badge ${ledger.entryType === 'CREDIT' ? 'badge-success' : 'badge-error'}`}>
                {ledger.entryType}
              </span>
            </div>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Account: <strong>{ledger.accountId}</strong></span>
              <span style={{ fontSize: '1.2rem', fontWeight: 'bold', color: ledger.entryType === 'CREDIT' ? 'var(--status-success)' : 'var(--status-error)' }}>
                ₹{ledger.amount.toFixed(2)}
              </span>
            </div>

            <div style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', background: 'rgba(0,0,0,0.2)', padding: '8px 12px', borderRadius: 'var(--radius-sm)' }}>
              {ledger.description}
            </div>
          </div>
        ))}
        {filteredLedgers.length === 0 && !loading && (
          <div className="glass-panel" style={{ padding: '32px', textAlign: 'center', gridColumn: '1 / -1', color: 'var(--text-secondary)' }}>
            {searchTerm ? `No ledger entries found matching "${searchTerm}".` : 'No ledger entries found.'}
          </div>
        )}
      </div>
    </div>
  );
}
