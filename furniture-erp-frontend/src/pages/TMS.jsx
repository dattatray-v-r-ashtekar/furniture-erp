import React, { useState, useEffect } from 'react';
import { Truck, MapPin, RefreshCw, CheckCircle, Navigation } from 'lucide-react';

export default function TMS() {
  const [routes, setRoutes] = useState([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const fetchRoutes = async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await fetch(`http://localhost:8081/api/v1/tms/routes`);
      if (!response.ok) throw new Error('Failed to fetch delivery routes');
      const data = await response.json();
      setRoutes(data);
    } catch (err) {
      setError('Failed to fetch delivery routes. Ensure the backend is running.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchRoutes();
  }, []);

  const handleStartRoute = async (routeId) => {
    try {
      await fetch(`http://localhost:8081/api/v1/tms/routes/${routeId}/start`, { method: 'POST' });
      fetchRoutes();
    } catch (err) {
      console.error(err);
    }
  };

  const handleCompleteStop = async (routeId, stopId) => {
    try {
      await fetch(`http://localhost:8081/api/v1/tms/routes/${routeId}/stops/${stopId}/complete`, { method: 'POST' });
      fetchRoutes();
    } catch (err) {
      console.error(err);
    }
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '24px', height: '100%' }}>
      <div className="glass-panel" style={{ padding: '24px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h2 style={{ fontSize: '1.5rem', display: 'flex', alignItems: 'center', gap: '12px' }}>
          <Truck /> Transport Management System
        </h2>
        <button className="btn btn-primary" onClick={fetchRoutes} disabled={loading}>
          <RefreshCw size={18} className={loading ? 'spin' : ''} /> Refresh
        </button>
      </div>

      {error && <div style={{ padding: '24px', color: 'var(--status-error)' }}>{error}</div>}

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(400px, 1fr))', gap: '24px' }}>
        {routes.map(routeData => (
          <div key={routeData.id} className="glass-panel animate-fade-in" style={{ padding: '32px', display: 'flex', flexDirection: 'column', gap: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid var(--border-glass)', paddingBottom: '16px' }}>
              <div>
                <p style={{ color: 'var(--text-secondary)', fontFamily: 'monospace', fontSize: '0.8rem' }}>{routeData.id}</p>
                <h3 style={{ marginTop: '8px', fontSize: '1.2rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  Driver ID: {routeData.driverId}
                </h3>
              </div>
              <div>
                <span className={`badge ${routeData.status === 'COMPLETED' ? 'badge-success' : 'badge-warning'}`} style={{ fontSize: '0.9rem', padding: '6px 12px' }}>
                  {routeData.status}
                </span>
              </div>
            </div>

            <div>
              <h3 style={{ marginBottom: '16px', fontSize: '1rem' }}>Delivery Route Stops</h3>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '12px' }}>
                {routeData.stops?.map((stop, i) => (
                  <div key={stop.id} style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', padding: '12px', background: 'rgba(0,0,0,0.2)', borderRadius: 'var(--radius-sm)' }}>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '16px' }}>
                      <div style={{ width: '28px', height: '28px', borderRadius: '50%', background: 'var(--bg-secondary)', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: '0.8rem' }}>{i + 1}</div>
                      <div>
                        <div style={{ fontWeight: 'bold', fontSize: '0.9rem', display: 'flex', alignItems: 'center', gap: '6px' }}>
                          <MapPin size={14} /> {stop.deliveryAddress}
                        </div>
                        <div style={{ fontSize: '0.75rem', color: 'var(--text-secondary)' }}>Sales Order: {stop.salesOrderId}</div>
                      </div>
                    </div>
                    <div style={{ display: 'flex', gap: '12px', alignItems: 'center' }}>
                      {stop.status === 'COMPLETED' ? (
                        <CheckCircle size={18} color="var(--status-success)" />
                      ) : (
                        routeData.status === 'IN_TRANSIT' && stop.status === 'PENDING' && (
                          <button className="btn btn-secondary" style={{ padding: '6px 12px', fontSize: '0.8rem' }} onClick={() => handleCompleteStop(routeData.id, stop.id)}>
                            Mark Delivered
                          </button>
                        )
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div style={{ display: 'flex', gap: '16px', marginTop: 'auto' }}>
              {routeData.status === 'PLANNED' && (
                <button className="btn btn-primary" style={{ width: '100%' }} onClick={() => handleStartRoute(routeData.id)}>
                  <Navigation size={18} /> Start Route
                </button>
              )}
            </div>
          </div>
        ))}
        {routes.length === 0 && !loading && (
          <div style={{ color: 'var(--text-secondary)', padding: '24px' }}>No delivery routes found.</div>
        )}
      </div>
    </div>
  );
}
