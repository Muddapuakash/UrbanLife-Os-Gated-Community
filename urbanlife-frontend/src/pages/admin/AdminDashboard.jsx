import { useEffect, useState } from 'react';
import DashboardLayout from '../../components/layout/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { dashboardApi } from '../../api/dashboardApi';
import { maintenanceBillApi } from '../../api/maintenanceBillApi';
import { parkingSlotApi } from '../../api/parkingSlotApi';
import { useAuth } from '../../auth/AuthContext';
import { useNavigate } from 'react-router-dom';

// ─── Mini bar chart component (pure CSS, no lib needed) ───────────────────────
function ProgressBar({ value, max, color = 'var(--primary)', label, sublabel }) {
  const pct = max > 0 ? Math.round((value / max) * 100) : 0;
  return (
    <div style={{ marginBottom: '14px' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '5px' }}>
        <span style={{ fontSize: '0.83rem', color: 'var(--text-secondary)' }}>{label}</span>
        <span style={{ fontSize: '0.83rem', fontWeight: 700, color }}>
          {value} {sublabel && <span style={{ fontWeight: 400, color: 'var(--text-muted)', fontSize: '0.78rem' }}>{sublabel}</span>}
        </span>
      </div>
      <div style={{ height: '7px', borderRadius: '999px', background: 'var(--border)' }}>
        <div style={{ height: '100%', borderRadius: '999px', background: color, width: `${pct}%`, transition: 'width 0.8s ease' }} />
      </div>
    </div>
  );
}

// ─── KPI Card ─────────────────────────────────────────────────────────────────
function KpiCard({ icon, label, value, sub, color = 'var(--primary)', onClick }) {
  return (
    <div
      onClick={onClick}
      style={{
        background: 'var(--bg-card)',
        border: '1px solid var(--border)',
        borderRadius: '14px',
        padding: '20px',
        cursor: onClick ? 'pointer' : 'default',
        transition: 'transform 0.15s, box-shadow 0.15s',
        position: 'relative',
        overflow: 'hidden'
      }}
      onMouseEnter={e => { if (onClick) { e.currentTarget.style.transform = 'translateY(-2px)'; e.currentTarget.style.boxShadow = `0 8px 30px ${color}22`; } }}
      onMouseLeave={e => { e.currentTarget.style.transform = ''; e.currentTarget.style.boxShadow = ''; }}
    >
      <div style={{ position: 'absolute', top: 0, left: 0, right: 0, height: '3px', background: color }} />
      <div style={{ fontSize: '1.6rem', marginBottom: '8px' }}>{icon}</div>
      <div style={{ fontSize: '2rem', fontWeight: 800, color, lineHeight: 1 }}>{value}</div>
      <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)', marginTop: '4px', fontWeight: 600 }}>{label}</div>
      {sub && <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '3px' }}>{sub}</div>}
    </div>
  );
}

// ─── Section Card ─────────────────────────────────────────────────────────────
function Section({ title, children, action }) {
  return (
    <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '14px', padding: '20px 24px', height: '100%' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '18px' }}>
        <h3 style={{ fontSize: '0.95rem', fontWeight: 700, color: 'var(--text-primary)' }}>{title}</h3>
        {action}
      </div>
      {children}
    </div>
  );
}

export default function AdminDashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const communityId = Number(user?.communityId || localStorage.getItem('communityId') || 1);

  const [data, setData] = useState(null);
  const [bills, setBills] = useState([]);
  const [slots, setSlots] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    setLoading(true);
    Promise.all([
      dashboardApi.getCommunityDashboard(communityId).catch(() => ({ data: null })),
      maintenanceBillApi.getByCommunity(communityId).catch(() => ({ data: [] })),
      parkingSlotApi.getByCommunity(communityId).catch(() => ({ data: [] }))
    ]).then(([dashRes, billRes, slotRes]) => {
      setData(dashRes.data);
      setBills(billRes.data || []);
      setSlots(slotRes.data || []);
    }).finally(() => setLoading(false));
  }, [communityId]);

  if (loading) return <DashboardLayout><LoadingSpinner /></DashboardLayout>;

  const d = data || {};

  // Bill analytics
  const paidBills = bills.filter(b => b.status === 'PAID').length;
  const pendingBills = bills.filter(b => b.status !== 'PAID').length;
  const collectionRate = bills.length > 0 ? Math.round((paidBills / bills.length) * 100) : 0;

  // Parking analytics
  const availSlots = slots.filter(s => s.status === 'AVAILABLE').length;
  const occupiedSlots = slots.filter(s => s.status === 'OCCUPIED').length;
  const parkingUtil = slots.length > 0 ? Math.round((occupiedSlots / slots.length) * 100) : 0;

  // Complaint resolution rate
  const complaintResRate = d.totalComplaints > 0 ? Math.round((d.resolvedComplaints / d.totalComplaints) * 100) : 0;

  const quickLinks = [
    { label: 'Post Notice', icon: '📢', path: '/admin/notices', color: '#6366f1' },
    { label: 'New Event', icon: '🎉', path: '/admin/events', color: '#10b981' },
    { label: 'Generate Bills', icon: '💵', path: '/admin/maintenance', color: '#f59e0b' },
    { label: 'Manage Complaints', icon: '🔧', path: '/admin/complaints', color: '#ef4444' },
    { label: 'Parking', icon: '🚗', path: '/admin/parking', color: '#3b82f6' },
    { label: 'Lost & Found', icon: '🔍', path: '/admin/lost-found', color: '#8b5cf6' },
  ];

  return (
    <DashboardLayout>
      {/* ─── Header ─────────────────────────────────────────────────────── */}
      <div style={{ marginBottom: '28px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <h1 style={{ fontSize: '1.6rem', fontWeight: 800, color: 'var(--text-primary)', margin: 0 }}>
              {d.communityName || 'Society Dashboard'} 🏘️
            </h1>
            <p style={{ color: 'var(--text-muted)', marginTop: '4px', fontSize: '0.88rem' }}>
              Community Admin Control Panel · Real-time overview
            </p>
          </div>
          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '8px', padding: '6px 12px' }}>
            📅 {new Date().toLocaleDateString('en-IN', { weekday: 'long', day: 'numeric', month: 'long', year: 'numeric' })}
          </div>
        </div>

        {/* Active Emergency Alert */}
        {d.activeEmergencies > 0 && (
          <div style={{ marginTop: '14px', background: 'rgba(239,68,68,0.12)', border: '1px solid rgba(239,68,68,0.4)', borderRadius: '10px', padding: '10px 16px', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{ fontSize: '1.2rem' }}>🚨</span>
            <span style={{ color: '#ef4444', fontWeight: 700, fontSize: '0.9rem' }}>
              {d.activeEmergencies} Active Emergency Alert{d.activeEmergencies > 1 ? 's' : ''} — Immediate attention required!
            </span>
          </div>
        )}
      </div>

      {/* ─── KPI Grid ───────────────────────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '24px' }}>
        <KpiCard icon="👥" label="Total Residents" value={d.totalResidents || 0}
          sub={`${d.activeResidents || 0} active`} color="#6366f1"
          onClick={() => navigate('/admin/residents')} />
        <KpiCard icon="🏠" label="Total Flats" value={d.totalFlats || 0}
          color="#10b981" onClick={() => navigate('/admin/blocks')} />
        <KpiCard icon="🔧" label="Pending Complaints" value={d.pendingComplaints || 0}
          sub={`${d.totalComplaints || 0} total`} color={d.pendingComplaints > 5 ? '#ef4444' : '#f59e0b'}
          onClick={() => navigate('/admin/complaints')} />
        <KpiCard icon="🚨" label="Active Emergencies" value={d.activeEmergencies || 0}
          sub={`${d.totalEmergencies || 0} total`} color={d.activeEmergencies > 0 ? '#ef4444' : '#6b7280'} />
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: '16px', marginBottom: '28px' }}>
        <KpiCard icon="🚗" label="Registered Vehicles" value={d.totalVehicles || 0} color="#3b82f6" />
        <KpiCard icon="👁️" label="Total Visitors" value={d.totalVisitors || 0} color="#8b5cf6" />
        <KpiCard icon="📋" label="Total Notices" value={d.totalNotices || 0} color="#14b8a6"
          onClick={() => navigate('/admin/notices')} />
        <KpiCard icon="🔍" label="Lost & Found Items" value={d.totalLostFoundItems || 0} color="#f97316"
          onClick={() => navigate('/admin/lost-found')} />
      </div>

      {/* ─── Middle Row: Analytics ───────────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '20px', marginBottom: '24px' }}>

        {/* Complaint Analytics */}
        <Section title="🔧 Complaint Analytics">
          <ProgressBar label="Pending" value={d.pendingComplaints || 0} max={d.totalComplaints || 1}
            color="#f59e0b" />
          <ProgressBar label="Resolved" value={d.resolvedComplaints || 0} max={d.totalComplaints || 1}
            color="#10b981" />
          <div style={{ marginTop: '16px', textAlign: 'center' }}>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: complaintResRate > 70 ? '#10b981' : '#f59e0b' }}>
              {complaintResRate}%
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>Resolution Rate</div>
          </div>
        </Section>

        {/* Billing Analytics */}
        <Section title="💰 Maintenance Collection">
          <ProgressBar label="Bills Paid" value={paidBills} max={bills.length || 1} color="#10b981" sublabel="flats" />
          <ProgressBar label="Pending" value={pendingBills} max={bills.length || 1} color="#ef4444" sublabel="flats" />
          <div style={{ marginTop: '16px', textAlign: 'center' }}>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: collectionRate > 70 ? '#10b981' : '#ef4444' }}>
              {collectionRate}%
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>Collection Rate</div>
          </div>
        </Section>

        {/* Parking Analytics */}
        <Section title="🅿️ Parking Utilization">
          <ProgressBar label="Occupied" value={occupiedSlots} max={slots.length || 1} color="#6366f1" sublabel="slots" />
          <ProgressBar label="Available" value={availSlots} max={slots.length || 1} color="#10b981" sublabel="slots" />
          <div style={{ marginTop: '16px', textAlign: 'center' }}>
            <div style={{ fontSize: '2.2rem', fontWeight: 800, color: 'var(--primary)' }}>
              {parkingUtil}%
            </div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>Parking Utilization</div>
          </div>
        </Section>
      </div>

      {/* ─── Bottom Row: Events/Polls + Quick Actions ────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>

        {/* Community Activity */}
        <Section title="📊 Community Activity">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            {[
              { icon: '🎉', label: 'Total Events', value: d.totalEvents || 0, sub: `${d.activeEvents || 0} active`, color: '#10b981' },
              { icon: '🗳️', label: 'Total Polls', value: d.totalPolls || 0, sub: `${d.activePolls || 0} active`, color: '#6366f1' },
            ].map(item => (
              <div key={item.label} style={{ background: 'var(--bg-main)', borderRadius: '10px', padding: '14px', textAlign: 'center' }}>
                <div style={{ fontSize: '1.4rem' }}>{item.icon}</div>
                <div style={{ fontSize: '1.6rem', fontWeight: 800, color: item.color }}>{item.value}</div>
                <div style={{ fontSize: '0.78rem', fontWeight: 600, color: 'var(--text-secondary)' }}>{item.label}</div>
                <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>{item.sub}</div>
              </div>
            ))}
          </div>

          <div style={{ marginTop: '16px', padding: '12px', background: 'var(--bg-main)', borderRadius: '10px', display: 'flex', justifyContent: 'space-between' }}>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.3rem', fontWeight: 800, color: '#f97316' }}>{d.totalLostFoundItems || 0}</div>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>Lost & Found</div>
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.3rem', fontWeight: 800, color: '#14b8a6' }}>{d.totalNotices || 0}</div>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>Notices Posted</div>
            </div>
            <div style={{ textAlign: 'center' }}>
              <div style={{ fontSize: '1.3rem', fontWeight: 800, color: '#8b5cf6' }}>{d.totalVisitors || 0}</div>
              <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>Total Visitors</div>
            </div>
          </div>
        </Section>

        {/* Quick Actions */}
        <Section title="⚡ Quick Actions">
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px' }}>
            {quickLinks.map(ql => (
              <button
                key={ql.path}
                onClick={() => navigate(ql.path)}
                style={{
                  background: `${ql.color}18`,
                  border: `1px solid ${ql.color}44`,
                  borderRadius: '10px',
                  padding: '12px',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '10px',
                  cursor: 'pointer',
                  transition: 'all 0.15s',
                  color: 'var(--text-primary)',
                  fontSize: '0.85rem',
                  fontWeight: 600
                }}
                onMouseEnter={e => { e.currentTarget.style.background = `${ql.color}28`; e.currentTarget.style.transform = 'scale(1.02)'; }}
                onMouseLeave={e => { e.currentTarget.style.background = `${ql.color}18`; e.currentTarget.style.transform = ''; }}
              >
                <span style={{ fontSize: '1.3rem' }}>{ql.icon}</span>
                {ql.label}
              </button>
            ))}
          </div>
        </Section>
      </div>
    </DashboardLayout>
  );
}
