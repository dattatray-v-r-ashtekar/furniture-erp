import React from 'react';
import { NavLink, Outlet, useLocation } from 'react-router-dom';
import { 
  ShoppingCart, Briefcase, Package, Factory, 
  Warehouse, Truck, Users, Activity, 
  Calculator, SearchCheck, BarChart3 
} from 'lucide-react';
import './index.css';

const navItems = [
  { path: '/', label: 'Storefront (B2C)', icon: ShoppingCart },
  { path: '/dealer', label: 'Dealer Portal (B2B)', icon: Briefcase },
  { path: '/mes', label: 'Manufacturing', icon: Factory },
  { path: '/inventory', label: 'Inventory', icon: Package },
  { path: '/wms', label: 'Warehouse (WMS)', icon: Warehouse },
  { path: '/procurement', label: 'Procurement', icon: Calculator },
  { path: '/tms', label: 'Logistics (TMS)', icon: Truck },
  { path: '/crm', label: 'CRM / Leads', icon: Users },
  { path: '/hrms', label: 'HR & Payroll', icon: Activity },
  { path: '/qms', label: 'Quality (QMS)', icon: SearchCheck },
  { path: '/accounting', label: 'Finance Ledger', icon: Calculator },
  { path: '/bi', label: 'BI & AI Insights', icon: BarChart3 }
];

export default function Layout() {
  const location = useLocation();

  const getPageTitle = () => {
    const item = navItems.find(n => n.path === location.pathname);
    return item ? item.label : 'Dashboard';
  };

  return (
    <div style={{ display: 'flex', minHeight: '100vh', width: '100%' }}>
      {/* Sidebar Navigation */}
      <aside className="glass-panel" style={{ width: '280px', margin: '16px', display: 'flex', flexDirection: 'column', padding: '24px' }}>
        <h2 style={{ marginBottom: '32px', fontSize: '1.5rem' }} className="gradient-text">Furniture ERP</h2>
        <nav style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
          {navItems.map((item) => {
            const Icon = item.icon;
            return (
              <NavLink 
                key={item.path} 
                to={item.path}
                style={({ isActive }) => ({
                  display: 'flex', alignItems: 'center', gap: '12px', padding: '12px 16px',
                  borderRadius: 'var(--radius-sm)', textDecoration: 'none',
                  color: isActive ? 'var(--text-primary)' : 'var(--text-secondary)',
                  background: isActive ? 'rgba(255, 255, 255, 0.08)' : 'transparent',
                  fontWeight: isActive ? 600 : 500,
                  border: isActive ? '1px solid var(--border-glass)' : '1px solid transparent',
                  transition: 'all 0.2s'
                })}
              >
                <Icon size={18} />
                {item.label}
              </NavLink>
            );
          })}
        </nav>
      </aside>

      {/* Main Content Area */}
      <main style={{ flex: 1, padding: '16px 16px 16px 0', display: 'flex', flexDirection: 'column' }}>
        <header className="glass-panel" style={{ padding: '20px 24px', marginBottom: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h1>{getPageTitle()}</h1>
          <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
            <span className="badge badge-success">API: Local Monolith (8081)</span>
            <div style={{ width: '40px', height: '40px', borderRadius: '50%', background: 'linear-gradient(135deg, var(--accent-primary), var(--accent-secondary))', display: 'flex', alignItems: 'center', justifyContent: 'center', fontWeight: 'bold' }}>AD</div>
          </div>
        </header>

        <div className="animate-fade-in" style={{ flex: 1 }}>
          <Outlet />
        </div>
      </main>
    </div>
  );
}
