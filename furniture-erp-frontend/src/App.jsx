import React from 'react'
import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './Layout'
import Ecommerce from './pages/Ecommerce'
import SalesOrders from './pages/SalesOrders'
import Manufacturing from './pages/Manufacturing'
import Accounting from './pages/Accounting'
import Inventory from './pages/Inventory'
import Procurement from './pages/Procurement'
import WMS from './pages/WMS'
import TMS from './pages/TMS'
import DealerPortal from './pages/DealerPortal'
import QMS from './pages/QMS'
import HRMS from './pages/HRMS'
import BI from './pages/BI'

// Placeholders for remaining modules
const Placeholder = ({ name }) => (
  <div className="glass-panel" style={{ padding: '40px', textAlign: 'center' }}>
    <h2>{name} Module</h2>
    <p style={{ color: 'var(--text-secondary)', marginTop: '16px' }}>This module is currently under construction in the E2E buildout phase.</p>
  </div>
)

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Ecommerce />} />
          <Route path="sales" element={<SalesOrders />} />
          <Route path="dealer" element={<DealerPortal />} />
          <Route path="mes" element={<Manufacturing />} />
          <Route path="inventory" element={<Inventory />} />
          <Route path="wms" element={<WMS />} />
          <Route path="procurement" element={<Procurement />} />
          <Route path="tms" element={<TMS />} />
          <Route path="crm" element={<Placeholder name="CRM & Leads" />} />
          <Route path="hrms" element={<HRMS />} />
          <Route path="qms" element={<QMS />} />
          <Route path="accounting" element={<Accounting />} />
          <Route path="bi" element={<BI />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
