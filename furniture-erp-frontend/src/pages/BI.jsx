import React, { useState, useEffect } from 'react';
import { BarChart3, TrendingUp, RefreshCw, FileText } from 'lucide-react';

export default function BI() {
  const [reports, setReports] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchReports = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/bi/reports`);
      if (!response.ok) throw new Error('Failed to fetch BI reports');
      const data = await response.json();
      setReports(data);
    } catch (err) {
      setError('Failed to fetch BI reports. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchReports();
  }, []);

  const generateReport = async () => {
    setLoading(true);
    try {
      const referenceCode = `MONTH-END-${new Date().getFullYear()}-${new Date().getMonth() + 1}`;
      await fetch('http://localhost:8081/api/v1/bi/reports', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ referenceCode })
      });
      fetchReports();
    } catch (err) {
      console.error(err);
      setError('Failed to generate report.');
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <BarChart3 /> Business Intelligence
        </h2>
        <button className="btn btn-primary" onClick={fetchReports} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      <div className="glass-panel" style={{ padding: '32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <FileText /> Executive Report Generation
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '8px' }}>
            Aggregates historical Kafka events (Revenue - Material Costs - Labor Costs) to show true Net Profit.
          </p>
        </div>
        <button className="btn btn-primary" onClick={generateReport} disabled={loading}>
          {loading ? 'Crunching Big Data...' : 'Generate Month-End Report'}
        </button>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))', gap: '24px' }}>
        {reports.map(report => (
          <div key={report.id} className="glass-panel animate-fade-in" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '12px' }}>
              <div>
                <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{report.id}</p>
                <h3 style={{ marginTop: '4px', fontSize: '1.1rem' }}>{report.referenceCode}</h3>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: '12px' }}>
              <div style={{ padding: '16px', background: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.2)', borderRadius: 'var(--radius-sm)' }}>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase' }}>Gross Revenue (Mock)</span>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--status-success)', marginTop: '4px' }}>
                  ₹15,000,000.00
                </div>
              </div>

              <div style={{ padding: '16px', background: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.2)', borderRadius: 'var(--radius-sm)' }}>
                <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase' }}>Operating Costs (Mock)</span>
                <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--status-error)', marginTop: '4px' }}>
                  ₹8,500,000.00
                </div>
              </div>

              <div style={{ padding: '16px', background: 'rgba(99, 102, 241, 0.05)', border: '1px solid rgba(99, 102, 241, 0.2)', borderRadius: 'var(--radius-sm)', display: 'flex', flexDirection: 'column' }}>
                 <span style={{ color: 'var(--text-secondary)', fontSize: '0.8rem', textTransform: 'uppercase' }}>Net Margin</span>
                 <div style={{ fontSize: '1.5rem', fontWeight: 'bold', color: 'var(--accent-primary)', marginTop: '4px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                   <TrendingUp size={20} />
                   43.3%
                 </div>
              </div>
            </div>

          </div>
        ))}
        {reports.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No BI reports generated yet.</div>
        )}
      </div>

    </div>
  );
}
