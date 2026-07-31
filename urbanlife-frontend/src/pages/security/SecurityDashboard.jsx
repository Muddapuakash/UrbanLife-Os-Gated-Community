import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardLayout from '../../components/layout/DashboardLayout';
import PageHeader from '../../components/common/PageHeader';
import { visitorApi } from '../../api/visitorApi';
import { parcelApi } from '../../api/parcelApi';
import { emergencyApi } from '../../api/emergencyApi';

const REFRESH_INTERVAL = 10000; // 10 seconds

export default function SecurityDashboard() {
  const navigate = useNavigate();
  const [stats, setStats] = useState({
    visitorsInside: 0,
    uncollectedParcels: 0,
    activeEmergencies: 0
  });
  const [lastUpdated, setLastUpdated] = useState(null);
  const [activeAlerts, setActiveAlerts] = useState([]);

  const fetchStats = useCallback(() => {
    Promise.allSettled([
      visitorApi.getByCommunity(1),
      parcelApi.getByCommunity(1),
      emergencyApi.getByCommunity(1)
    ]).then(([visitorsRes, parcelsRes, emergenciesRes]) => {
      const visitors = visitorsRes.status === 'fulfilled' ? visitorsRes.value.data : [];
      const parcels = parcelsRes.status === 'fulfilled' ? parcelsRes.value.data : [];
      const emergencies = emergenciesRes.status === 'fulfilled' ? emergenciesRes.value.data : [];

      const visitorsInside = visitors.filter(v => v.status === 'CHECKED_IN' || v.status === 'INSIDE').length;
      const uncollectedParcels = parcels.filter(p => p.status === 'RECEIVED' || p.status === 'NOTIFIED' || p.status === 'PENDING').length;
      const activeEmergencyList = emergencies.filter(e =>
        e.status !== 'RESOLVED' && e.status !== 'CLOSED' && e.status !== 'FALSE_ALARM'
      );

      setStats({
        visitorsInside,
        uncollectedParcels,
        activeEmergencies: activeEmergencyList.length
      });
      setActiveAlerts(activeEmergencyList);
      setLastUpdated(new Date());
    });
  }, []);

  useEffect(() => {
    fetchStats();
    const interval = setInterval(fetchStats, REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [fetchStats]);

  const formatTime = (date) => {
    if (!date) return '';
    return date.toLocaleTimeString('en-IN', { hour: '2-digit', minute: '2-digit', second: '2-digit' });
  };

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
        <PageHeader 
          title="Security Gate Control Desk" 
          subtitle="Live gate access counter, visitor check-in scanner & emergency alert desk" 
        />
        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '6px', whiteSpace: 'nowrap' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', display: 'inline-block', animation: 'pulse 2s infinite' }}></span>
          Live · Updated {lastUpdated ? formatTime(lastUpdated) : '...'}
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card" onClick={() => navigate('/security/visitors')} style={{ cursor: 'pointer' }}>
          <div className="stat-icon">👥</div>
          <div className="stat-info">
            <h4>Visitors Inside</h4>
            <div className="stat-value">{stats.visitorsInside}</div>
          </div>
        </div>
        <div className="stat-card" onClick={() => navigate('/security/parcels')} style={{ cursor: 'pointer' }}>
          <div className="stat-icon">📦</div>
          <div className="stat-info">
            <h4>Uncollected Parcels</h4>
            <div className="stat-value">{stats.uncollectedParcels}</div>
          </div>
        </div>
        <div className="stat-card" onClick={() => navigate('/security/emergencies')} style={{ cursor: 'pointer' }}>
          <div className="stat-icon">🚨</div>
          <div className="stat-info">
            <h4>Active SOS Alerts</h4>
            <div className="stat-value" style={{ color: stats.activeEmergencies > 0 ? 'var(--danger)' : 'inherit' }}>
              {stats.activeEmergencies}
            </div>
          </div>
        </div>
      </div>

      {/* Active Emergency Alerts Banner */}
      {activeAlerts.length > 0 && (
        <div style={{ marginBottom: '20px' }}>
          {activeAlerts.map(alert => (
            <div
              key={alert.emergencyId || alert.id}
              onClick={() => navigate('/security/emergencies')}
              style={{
                padding: '14px 18px',
                background: 'rgba(239,68,68,0.12)',
                border: '1px solid var(--danger)',
                borderRadius: '8px',
                marginBottom: '8px',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: '12px',
                animation: 'pulse 2s infinite'
              }}
            >
              <span style={{ fontSize: '1.4rem' }}>🚨</span>
              <div>
                <strong style={{ color: 'var(--danger)', display: 'block' }}>
                  ACTIVE EMERGENCY — {alert.emergencyType}
                </strong>
                <span style={{ fontSize: '0.82rem', color: 'var(--text-muted)' }}>
                  📍 {alert.locationDetails} &nbsp;|&nbsp; Priority: {alert.priority} &nbsp;|&nbsp; Status: {alert.status}
                </span>
              </div>
              <span style={{ marginLeft: 'auto', fontSize: '0.75rem', color: 'var(--danger)', fontWeight: 600 }}>
                Click to Respond →
              </span>
            </div>
          ))}
        </div>
      )}

      <div className="card">
        <h3>Gate Security Actions</h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '12px', marginTop: '16px' }}>
          {[
            { icon: '🔍', label: 'Verify Visitor Pass', path: '/security/visitors' },
            { icon: '📦', label: 'Log Delivery Package', path: '/security/parcels' },
            { icon: '🧑‍🔧', label: 'Staff Entry / Exit', path: '/security/staff-entry' },
            { icon: '🚨', label: 'Emergency Response', path: '/security/emergencies' }
          ].map(action => (
            <button
              key={action.path}
              onClick={() => navigate(action.path)}
              style={{
                background: 'var(--card-bg)',
                border: '1px solid var(--border)',
                borderRadius: '10px',
                padding: '14px',
                cursor: 'pointer',
                textAlign: 'center',
                color: 'var(--text)',
                transition: 'all 0.2s'
              }}
              onMouseEnter={e => e.currentTarget.style.borderColor = 'var(--primary)'}
              onMouseLeave={e => e.currentTarget.style.borderColor = 'var(--border)'}
            >
              <div style={{ fontSize: '1.6rem', marginBottom: '6px' }}>{action.icon}</div>
              <div style={{ fontSize: '0.82rem', fontWeight: 600 }}>{action.label}</div>
            </button>
          ))}
        </div>
      </div>
    </DashboardLayout>
  );
}
