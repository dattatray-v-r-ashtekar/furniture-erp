import React, { useState } from 'react';
import { Calculator, Search, ArrowRightLeft } from 'lucide-react';

export default function Accounting() {
  const [ledgerId, setLedgerId] = useState('');
  const [ledgerData, setLedgerData] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchLedger = async () => {
    if (!ledgerId) return;
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/accounting/ledger/${ledgerId}`);
      if (!response.ok) throw new Error('Ledger entry not found');
      const data = await response.json();
      setLedgerData(data);
    } catch (err) {
      setError('Failed to fetch ledger entry. Ensure the ID is correct.');
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
            placeholder="Enter Ledger UUID to view transaction..." 
            value={ledgerId}
            onChange={(e) => setLedgerId(e.target.value)}
          />
          <button className="btn btn-primary" onClick={fetchLedger}>
            <Search size={18} /> Lookup
          </button>
        </div>
      </div>

      {loading && <div style={{ padding: '24px', textAlign: 'center' }}>Loading...</div>}
      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      {ledgerData && (
        <div className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
            <div>
              <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
                <Calculator /> General Ledger Entry
              </h2>
              <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', marginTop: '8px' }}>{ledgerData.id}</p>
            </div>
            <div>
              <span className={`badge ${ledgerData.entryType === 'CREDIT' ? 'badge-success' : 'badge-error'}`} style={{ fontSize: '1rem', padding: '8px 16px' }}>
                {ledgerData.entryType}
              </span>
            </div>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Account ID</span>
              <span style={{ fontSize: '1.2rem', fontWeight: 'bold' }}>{ledgerData.accountId}</span>
            </div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Amount</span>
              <span style={{ fontSize: '1.5rem', fontWeight: 'bold', color: ledgerData.entryType === 'CREDIT' ? 'var(--status-success)' : 'var(--status-error)' }}>
                ${ledgerData.amount.toFixed(2)}
              </span>
            </div>
          </div>

          <div>
            <h3 style={{ marginBottom: '16px', color: 'var(--text-secondary)' }}>Description</h3>
            <div style={{ fontSize: '1.1rem', background: 'rgba(0,0,0,0.2)', padding: '16px', borderRadius: 'var(--radius-sm)' }}>
              {ledgerData.description}
            </div>
          </div>
          
        </div>
      )}
    </div>
  );
}
