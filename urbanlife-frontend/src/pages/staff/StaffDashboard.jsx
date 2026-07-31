import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardLayout from '../../components/layout/DashboardLayout';
import PageHeader from '../../components/common/PageHeader';
import { complaintApi } from '../../api/complaintApi';

const REFRESH_INTERVAL = 10000;

export default function StaffDashboard() {
  const navigate = useNavigate();
  const staffId = Number(localStorage.getItem('userId')) || 1;

  const [stats, setStats] = useState({
    assigned: 0,
    inProgress: 0,
    resolved: 0
  });
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  const fetchStats = useCallback(() => {
    complaintApi.getByAssignedStaff(staffId)
      .then(res => {
        const list = res.data || [];
        setStats({
          assigned: list.filter(c => c.status === 'ASSIGNED' || c.status === 'OPEN').length,
          inProgress: list.filter(c => c.status === 'IN_PROGRESS').length,
          resolved: list.filter(c => c.status === 'RESOLVED' || c.status === 'CLOSED').length
        });
        setLastUpdated(new Date());
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [staffId]);

  useEffect(() => {
    fetchStats();
    const interval = setInterval(fetchStats, REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [fetchStats]);

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
        <PageHeader 
          title="Staff Workbench" 
          subtitle="Manage assigned maintenance tickets, plumbing/electrical tasks & emergency response" 
        />
        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '6px', whiteSpace: 'nowrap' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', display: 'inline-block' }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      <div className="stats-grid">
        <div className="stat-card" onClick={() => navigate('/staff/complaints')} style={{ cursor: 'pointer' }}>
          <div className="stat-icon">🔧</div>
          <div className="stat-info">
            <h4>Assigned Tickets</h4>
            <div className="stat-value">{stats.assigned}</div>
          </div>
        </div>
        <div className="stat-card" onClick={() => navigate('/staff/complaints')} style={{ cursor: 'pointer' }}>
          <div className="stat-icon">⏳</div>
          <div className="stat-info">
            <h4>In Progress</h4>
            <div className="stat-value">{stats.inProgress}</div>
          </div>
        </div>
        <div className="stat-card" onClick={() => navigate('/staff/complaints')} style={{ cursor: 'pointer' }}>
          <div className="stat-icon">✅</div>
          <div className="stat-info">
            <h4>Resolved Tickets</h4>
            <div className="stat-value">{stats.resolved}</div>
          </div>
        </div>
      </div>

      <div className="card">
        <h3>Staff Work Instructions</h3>
        <p style={{ color: 'var(--text-muted)', marginTop: '8px', lineHeight: '1.6' }}>
          1. Go to <strong>My Assigned Work Tickets</strong> from the left sidebar.<br />
          2. When starting a maintenance task (plumbing, electrician, elevator, woodworking), click <strong>Start Work</strong> (Status: <code>IN_PROGRESS</code>).<br />
          3. Once the issue is fixed at the resident's flat, click <strong>Mark Resolved</strong> (Status: <code>RESOLVED</code>).
        </p>
      </div>
    </DashboardLayout>
  );
}
