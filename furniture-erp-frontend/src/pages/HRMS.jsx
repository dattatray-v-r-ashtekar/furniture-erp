import React, { useState, useEffect } from 'react';
import { Activity, UserPlus, Clock, CheckCircle, Users, RefreshCw } from 'lucide-react';

export default function HRMS() {
  const [employees, setEmployees] = useState([]);
  const [slips, setSlips] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const [empName, setEmpName] = useState('Sarah Connor');
  const [role, setRole] = useState('MACHINE_OPERATOR');
  
  const [status, setStatus] = useState(null);

  const fetchData = async () => {
    setLoading(true);
    setError(null);
    try {
      const [empRes, slipRes] = await Promise.all([
        fetch('http://localhost:8081/api/v1/hrms/employees'),
        fetch('http://localhost:8081/api/v1/payroll/slips')
      ]);
      
      if (!empRes.ok || !slipRes.ok) throw new Error('Failed to fetch HRMS data');
      
      const empData = await empRes.json();
      const slipData = await slipRes.json();
      
      setEmployees(empData);
      setSlips(slipData);
    } catch (err) {
      setError('Failed to fetch data. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const onboardEmployee = async () => {
    setStatus('processing');
    try {
      const referenceCode = `EMP-${empName.replace(/\s+/g, '-').toUpperCase()}-${role}`;
      await fetch('http://localhost:8081/api/v1/hrms/employees', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ referenceCode })
      });
      setStatus('onboarded');
      fetchData();
      setTimeout(() => setStatus('idle'), 3000);
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const runPayroll = async () => {
    setStatus('processing');
    try {
      const referenceCode = `PAYROLL-WEEK-${new Date().getWeek() || '42'}`;
      await fetch('http://localhost:8081/api/v1/payroll/slips', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ referenceCode })
      });
      setStatus('payroll_run');
      fetchData();
      setTimeout(() => setStatus('idle'), 3000);
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  // Helper to get week number
  Date.prototype.getWeek = function() {
    var date = new Date(this.getTime());
    date.setHours(0, 0, 0, 0);
    date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
    var week1 = new Date(date.getFullYear(), 0, 4);
    return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000 - 3 + (week1.getDay() + 6) % 7) / 7);
  }

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Users /> HR & Payroll (HRMS)
        </h2>
        <button className="btn btn-primary" onClick={fetchData} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px' }}>
        
        {/* Employee Onboarding */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
            <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}><UserPlus /> 1. Onboard Employee</h3>
            <div className="form-group"><label className="form-label">Name</label><input type="text" className="form-input" value={empName} onChange={e => setEmpName(e.target.value)} /></div>
            <div className="form-group"><label className="form-label">Role</label><input type="text" className="form-input" value={role} onChange={e => setRole(e.target.value)} /></div>
            <button className="btn btn-primary" onClick={onboardEmployee}>Onboard via HRMS</button>
            
            {status === 'onboarded' && (
              <div style={{ color: 'var(--status-success)', fontSize: '0.85rem' }}>EmployeeOnboardedEvent fired!</div>
            )}
          </div>

          <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px', flex: 1 }}>
            <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>Employee List</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', overflowY: 'auto', maxHeight: '300px' }}>
              {employees.map(emp => (
                <div key={emp.id} style={{ display: 'flex', flexDirection: 'column', padding: '12px', background: 'rgba(255,255,255,0.05)', borderRadius: 'var(--radius-sm)' }}>
                  <span style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', fontFamily: 'monospace' }}>{emp.id}</span>
                  <span style={{ fontWeight: 'bold' }}>{emp.referenceCode}</span>
                </div>
              ))}
              {employees.length === 0 && <span style={{ color: 'var(--text-secondary)' }}>No employees found.</span>}
            </div>
          </div>
        </div>

        {/* Payroll Run */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
          <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
             <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}><Activity /> 2. Run Payroll</h3>
             <p style={{ color: 'var(--text-secondary)' }}>Execute global payroll across all factory workers.</p>
             
             <button className="btn btn-primary" style={{ marginTop: '16px', background: 'linear-gradient(135deg, #10b981, #059669)' }} onClick={runPayroll}>
               Execute Global Payroll Run
             </button>

             {status === 'payroll_run' && (
              <div className="animate-fade-in" style={{ marginTop: '12px', padding: '16px', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid var(--status-success)', borderRadius: 'var(--radius-sm)' }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-success)', fontWeight: 'bold', marginBottom: '8px' }}>
                  <CheckCircle size={18} /> Payroll Executed!
                </div>
                <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
                  Payroll service generated payslips and fired a massive PayrollRunCompletedEvent. Accounting will now log a massive liability for wages.
                </p>
              </div>
            )}
          </div>

          <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px', flex: 1 }}>
            <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>Salary Slips</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '12px', overflowY: 'auto', maxHeight: '300px' }}>
              {slips.map(slip => (
                <div key={slip.id} style={{ display: 'flex', flexDirection: 'column', padding: '12px', background: 'rgba(255,255,255,0.05)', borderRadius: 'var(--radius-sm)' }}>
                  <span style={{ color: 'var(--text-secondary)', fontSize: '0.75rem', fontFamily: 'monospace' }}>{slip.id}</span>
                  <span style={{ fontWeight: 'bold' }}>{slip.referenceCode}</span>
                </div>
              ))}
              {slips.length === 0 && <span style={{ color: 'var(--text-secondary)' }}>No salary slips generated yet.</span>}
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}
