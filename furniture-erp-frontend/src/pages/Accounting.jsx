import React, { useState, useEffect } from 'react';
import { Calculator, RefreshCw } from 'lucide-react';

export default function Accounting() {
  const [ledgers, setLedgers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

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

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Calculator /> General Ledger
        </h2>
        <button className="btn btn-primary" onClick={fetchLedgers} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '16px' }}>
        {ledgers.map(ledger => (
          <div key={ledger.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '12px' }}>
              <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{ledger.id}</p>
              <span className={`badge ${ledger.entryType === 'CREDIT' ? 'badge-success' : 'badge-error'}`}>
                {ledger.entryType}
              </span>
            </div>
            
            <div style={{ display: 'flex', justifyContent: 'space-between' }}>
              <span style={{ color: 'var(--text-secondary)' }}>Account: {ledger.accountId}</span>
              <span style={{ fontSize: '1.2rem', fontWeight: 'bold', color: ledger.entryType === 'CREDIT' ? 'var(--status-success)' : 'var(--status-error)' }}>
                ₹{ledger.amount.toFixed(2)}
              </span>
            </div>

            <div style={{ fontSize: '0.9rem', color: 'var(--text-secondary)', background: 'rgba(0,0,0,0.2)', padding: '8px', borderRadius: 'var(--radius-sm)' }}>
              {ledger.description}
            </div>
          </div>
        ))}
        {ledgers.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No ledger entries found.</div>
        )}
      </div>
    </div>
  );
}
