import React, { useState } from 'react';
import { SearchCheck, AlertTriangle } from 'lucide-react';

export default function QMS() {
  const [workOrderId, setWorkOrderId] = useState('');
  const [reason, setReason] = useState('Wobbly legs');
  const [status, setStatus] = useState(null);

  const logDefect = async () => {
    setStatus('processing');
    try {
      const payload = {
        workOrderId,
        status: 'FAILED',
        defectReason: reason
      };
      
      await fetch('http://localhost:8081/api/v1/qms/inspections', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
      });
      
      setStatus('success');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%', maxWidth: '800px' }}>
      <div className="glass-panel" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
          <SearchCheck /> Quality Management System (QMS)
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
    </div>
  );
}
