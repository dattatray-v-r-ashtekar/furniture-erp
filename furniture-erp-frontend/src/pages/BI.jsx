import React, { useState } from 'react';
import { BarChart3, TrendingUp } from 'lucide-react';

export default function BI() {
  const [report, setReport] = useState(null);
  const [loading, setLoading] = useState(false);

  const generateReport = async () => {
    setLoading(true);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/bi/reports/daily-revenue`);
      const data = await response.json();
      setReport(data);
    } catch (err) {
      console.error(err);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%', maxWidth: '900px' }}>
      
      <div className="glass-panel" style={{ padding: '32px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <div>
          <h2 style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
            <BarChart3 /> Business Intelligence Executive Report
          </h2>
          <p style={{ color: 'var(--text-secondary)', marginTop: '8px' }}>
            Aggregates historical Kafka events (Revenue - Material Costs - Labor Costs) to show true Net Profit.
          </p>
        </div>
        <button className="btn btn-primary" onClick={generateReport}>
          {loading ? 'Crunching Big Data...' : 'Generate Month-End Report'}
        </button>
      </div>

      {report && (
        <div className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '32px' }}>
          
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '24px' }}>
            
            <div style={{ padding: '24px', background: 'rgba(16, 185, 129, 0.05)', border: '1px solid rgba(16, 185, 129, 0.2)', borderRadius: 'var(--radius-md)' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', textTransform: 'uppercase' }}>Gross Revenue</span>
              <div style={{ fontSize: '2.5rem', fontWeight: 'bold', color: 'var(--status-success)', marginTop: '8px' }}>
                ${report.totalRevenue?.toFixed(2)}
              </div>
            </div>

            <div style={{ padding: '24px', background: 'rgba(239, 68, 68, 0.05)', border: '1px solid rgba(239, 68, 68, 0.2)', borderRadius: 'var(--radius-md)' }}>
              <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', textTransform: 'uppercase' }}>Operating Costs</span>
              <div style={{ fontSize: '2.5rem', fontWeight: 'bold', color: 'var(--status-error)', marginTop: '8px' }}>
                ${(report.materialCosts || 0 + report.laborCosts || 0).toFixed(2)}
              </div>
            </div>

            <div style={{ padding: '24px', background: 'rgba(99, 102, 241, 0.05)', border: '1px solid rgba(99, 102, 241, 0.2)', borderRadius: 'var(--radius-md)', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
               <span style={{ color: 'var(--text-secondary)', fontSize: '0.9rem', textTransform: 'uppercase' }}>Net Margin</span>
               <div style={{ fontSize: '2.5rem', fontWeight: 'bold', color: 'var(--accent-primary)', marginTop: '8px', display: 'flex', alignItems: 'center', gap: '8px' }}>
                 <TrendingUp size={32} />
                 {((report.totalRevenue - (report.materialCosts || 0 + report.laborCosts || 0)) / report.totalRevenue * 100).toFixed(1)}%
               </div>
            </div>

          </div>
          
          <div>
            <h3 style={{ marginBottom: '16px', color: 'var(--text-secondary)' }}>Raw Aggregation Data</h3>
            <pre style={{ fontSize: '0.85rem', background: 'rgba(0,0,0,0.3)', padding: '16px', borderRadius: 'var(--radius-sm)', overflowX: 'auto' }}>
              {JSON.stringify(report, null, 2)}
            </pre>
          </div>
          
        </div>
      )}
    </div>
  );
}
