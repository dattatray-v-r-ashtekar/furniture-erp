import React, { useState } from 'react';
import { Activity, UserPlus, Clock, CheckCircle } from 'lucide-react';

export default function HRMS() {
  const [empName, setEmpName] = useState('Sarah Connor');
  const [role, setRole] = useState('MACHINE_OPERATOR');
  const [hourlyRate, setHourlyRate] = useState(30.00);
  
  const [shiftName, setShiftName] = useState('Sarah Connor');
  const [hours, setHours] = useState(8);
  
  const [status, setStatus] = useState(null);
  const [log, setLog] = useState(null);

  const onboardEmployee = async () => {
    setStatus('processing');
    try {
      await fetch('http://localhost:8081/api/v1/hrms/employees', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ firstName: empName.split(' ')[0], lastName: empName.split(' ')[1] || '', role, hourlyRate })
      });
      setStatus('onboarded');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const completeShift = async () => {
    setStatus('processing');
    try {
      await fetch('http://localhost:8081/api/v1/hrms/shifts/complete', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ employeeName: shiftName, hoursWorked: parseInt(hours) })
      });
      setStatus('shift_logged');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  const runPayroll = async () => {
    setStatus('processing');
    try {
      const res = await fetch('http://localhost:8081/api/v1/payroll/run', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ periodStart: '2026-08-01', periodEnd: '2026-08-07' })
      });
      const data = await res.json();
      setLog(data);
      setStatus('payroll_run');
    } catch (error) {
      console.error(error);
      setStatus('error');
    }
  };

  return (
    <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '24px', height: '100%' }}>
      
      {/* Employee Onboarding & Shifts */}
      <div style={{ display: 'flex', flexDirection: 'column', gap: '24px' }}>
        <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}><UserPlus /> 1. Onboard Employee</h3>
          <div className="form-group"><label className="form-label">Name</label><input type="text" className="form-input" value={empName} onChange={e => setEmpName(e.target.value)} /></div>
          <div className="form-group"><label className="form-label">Role</label><input type="text" className="form-input" value={role} onChange={e => setRole(e.target.value)} /></div>
          <div className="form-group"><label className="form-label">Hourly Rate ($)</label><input type="number" className="form-input" value={hourlyRate} onChange={e => setHourlyRate(e.target.value)} /></div>
          <button className="btn btn-primary" onClick={onboardEmployee}>Onboard via HRMS</button>
          
          {status === 'onboarded' && (
            <div style={{ color: 'var(--status-success)', fontSize: '0.85rem' }}>EmployeeOnboardedEvent fired! Payroll service created tax profile.</div>
          )}
        </div>

        <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
          <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}><Clock /> 2. Log Completed Shift</h3>
          <div className="form-group"><label className="form-label">Employee Name</label><input type="text" className="form-input" value={shiftName} onChange={e => setShiftName(e.target.value)} /></div>
          <div className="form-group"><label className="form-label">Hours Worked</label><input type="number" className="form-input" value={hours} onChange={e => setHours(e.target.value)} /></div>
          <button className="btn btn-primary" onClick={completeShift}>Log Shift</button>

          {status === 'shift_logged' && (
            <div style={{ color: 'var(--status-success)', fontSize: '0.85rem' }}>ShiftCompletedEvent fired! Payroll service calculated wages.</div>
          )}
        </div>
      </div>

      {/* Payroll Run */}
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', flexDirection: 'column', gap: '16px' }}>
         <h3 style={{ display: 'flex', alignItems: 'center', gap: '8px' }}><Activity /> 3. Run Weekly Payroll</h3>
         <p style={{ color: 'var(--text-secondary)' }}>Execute global payroll across all factory workers.</p>
         
         <button className="btn btn-primary" style={{ marginTop: '16px', background: 'linear-gradient(135deg, #10b981, #059669)' }} onClick={runPayroll}>
           Execute Global Payroll Run
         </button>

         {status === 'payroll_run' && (
          <div className="animate-fade-in" style={{ marginTop: '24px', padding: '16px', background: 'rgba(16, 185, 129, 0.1)', border: '1px solid var(--status-success)', borderRadius: 'var(--radius-sm)' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '8px', color: 'var(--status-success)', fontWeight: 'bold', marginBottom: '8px' }}>
              <CheckCircle size={18} /> Payroll Executed!
            </div>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>
              Payroll service generated payslips and fired a massive PayrollRunCompletedEvent. Accounting will now log a massive liability for wages.
            </p>
            <pre style={{ marginTop: '12px', fontSize: '0.75rem', background: 'rgba(0,0,0,0.3)', padding: '8px', borderRadius: '4px', overflowX: 'auto' }}>
              {JSON.stringify(log, null, 2)}
            </pre>
          </div>
        )}
      </div>

    </div>
  );
}
