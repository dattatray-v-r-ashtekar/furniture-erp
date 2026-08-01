import React, { useState, useEffect } from 'react';
import { SearchCheck, AlertTriangle, RefreshCw } from 'lucide-react';

export default function QMS() {
  const [inspections, setInspections] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [workOrderId, setWorkOrderId] = useState('');
  const [reason, setReason] = useState('Wobbly legs');
  const [status, setStatus] = useState(null);

  const fetchInspections = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/qms/inspections`);
      if (!response.ok) throw new Error('Failed to fetch inspections');
      const data = await response.json();
      setInspections(data);
    } catch (err) {
      setError('Failed to fetch inspections. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchInspections();
  }, []);

  const logDefect = async () => {
    setStatus('processing');
    try {
      const payload = {
        referenceCode: workOrderId
      };
      
      await fetch('http://localhost:8081/api/v1/qms/inspections', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      setStatus('success');
      fetchInspections();
      setTimeout(() => setStatus('idle'), 3000);
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <SearchCheck /> Quality Management System
        </h2>
        <button className="btn btn-primary" onClick={fetchInspections} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      <div className="glass-panel" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <AlertTriangle /> Log Quality Inspection
        </h2>
        <p style={{ color: 'var(--text-secondary)' }}>Log factory defects and trigger quarantine processes. (Scenario 3)</p>

        <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '16px' }}>
          <div className="form-group">
            <label className="form-label">Completed WorkOrder ID (from MES)</label>
            <input type="text" className="form-input" placeholder="Enter UUID..." value={workOrderId} onChange={e => setWorkOrderId(e.target.value)} />
          </div>
          <div className="form-group">
            <label className="form-label">Defect Reason</label>
            <input type="text" className="form-input" value={reason} onChange={e => setReason(e.target.value)} />
          </div>
        </div>

        <button 
          className="btn btn-primary" 
          style={{ background: 'linear-gradient(135deg, var(--status-error), #b91c1c)' }}
          onClick={logDefect}
          disabled={status === 'processing' || !workOrderId}
        >
          <AlertTriangle size={18} /> Log Critical Defect
        </button>

        {status === 'success' && (
          <div className="animate-fade-in" style={{ padding: '16px', background: 'rgba(239, 68, 68, 0.1)', border: '1px solid var(--status-error)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-error)', fontWeight: 'bold' }}>
              <AlertTriangle size={18} /> Defect Logged! InspectionFailedEvent Fired!
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '8px' }}>
              Inventory will now catch this event and move the goods into Quarantine.
            </p>
          </div>
        )}
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: '24px' }}>
        {inspections.map(inspection => (
          <div key={inspection.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '12px' }}>
              <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{inspection.id}</p>
            </div>
            
            <div style={{ display: 'flex', flexDirection: 'column', gap: '4px' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem' }}>Reference Code</span>
              <span style={{ fontSize: '1.1rem', fontWeight: 'bold' }}>{inspection.referenceCode}</span>
            </div>
          </div>
        ))}
        {inspections.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No quality inspections logged yet.</div>
        )}
      </div>
    </div>
  );
}
