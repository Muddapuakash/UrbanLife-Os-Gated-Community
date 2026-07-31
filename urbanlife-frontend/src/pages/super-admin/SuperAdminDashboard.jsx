import { useEffect, useState } from 'react';
import DashboardLayout from '../../components/layout/DashboardLayout';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import { communityApi } from '../../api/communityApi';
import { userApi } from '../../api/userApi';
import { dashboardApi } from '../../api/dashboardApi';
import { useNavigate } from 'react-router-dom';

// ─── Subcomponents ────────────────────────────────────────────────────────────
function PlatformKpi({ icon, label, value, sub, color }) {
  return (
    <div style={{
      background: 'var(--bg-card)',
      border: '1px solid var(--border)',
      borderLeft: `4px solid ${color}`,
      borderRadius: '14px',
      padding: '20px 22px',
      position: 'relative',
      overflow: 'hidden'
    }}>
      <div style={{ position: 'absolute', right: '16px', top: '16px', fontSize: '2rem', opacity: 0.15 }}>{icon}</div>
      <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase', letterSpacing: '0.05em' }}>{label}</div>
      <div style={{ fontSize: '2.4rem', fontWeight: 900, color, lineHeight: 1.1, marginTop: '6px' }}>{value}</div>
      {sub && <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '4px' }}>{sub}</div>}
    </div>
  );
}

function CommunityHealthCard({ community, dashboard, onClick }) {
  const d = dashboard || {};
  const resolutionRate = d.totalComplaints > 0
    ? Math.round((d.resolvedComplaints / d.totalComplaints) * 100) : 100;

  const healthScore = Math.round(
    (resolutionRate * 0.4) +
    ((d.activeResidents || 0) > 0 ? 60 : 0) * 0.3 +
    (d.activeEmergencies === 0 ? 30 : 0)
  );

  const healthColor = healthScore >= 70 ? '#10b981' : healthScore >= 40 ? '#f59e0b' : '#ef4444';
  const healthLabel = healthScore >= 70 ? 'Healthy' : healthScore >= 40 ? 'Needs Attention' : 'Critical';

  return (
    <div
      onClick={onClick}
      style={{
        background: 'var(--bg-card)',
        border: '1px solid var(--border)',
        borderRadius: '14px',
        padding: '20px',
        cursor: 'pointer',
        transition: 'all 0.2s',
      }}
      onMouseEnter={e => { e.currentTarget.style.borderColor = healthColor; e.currentTarget.style.transform = 'translateY(-2px)'; }}
      onMouseLeave={e => { e.currentTarget.style.borderColor = 'var(--border)'; e.currentTarget.style.transform = ''; }}
    >
      {/* Header */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '16px' }}>
        <div>
          <div style={{ fontWeight: 700, fontSize: '1rem', color: 'var(--text-primary)' }}>
            🏘️ {community.communityName || community.name}
          </div>
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '2px' }}>
            ID #{community.communityId || community.id}
          </div>
        </div>
        <div style={{
          background: `${healthColor}20`,
          color: healthColor,
          fontSize: '0.72rem',
          fontWeight: 700,
          padding: '4px 10px',
          borderRadius: '999px',
          border: `1px solid ${healthColor}44`
        }}>
          {healthLabel}
        </div>
      </div>

      {/* Stats Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '10px', marginBottom: '14px' }}>
        {[
          { label: 'Residents', value: d.totalResidents || 0, icon: '👥' },
          { label: 'Flats', value: d.totalFlats || 0, icon: '🏠' },
          { label: 'Vehicles', value: d.totalVehicles || 0, icon: '🚗' },
          { label: 'Complaints', value: d.pendingComplaints || 0, icon: '🔧' },
          { label: 'Events', value: d.activeEvents || 0, icon: '🎉' },
          { label: 'Emergencies', value: d.activeEmergencies || 0, icon: d.activeEmergencies > 0 ? '🚨' : '✅' },
        ].map(s => (
          <div key={s.label} style={{ background: 'var(--bg-main)', borderRadius: '8px', padding: '8px', textAlign: 'center' }}>
            <div style={{ fontSize: '0.9rem' }}>{s.icon}</div>
            <div style={{ fontSize: '1.1rem', fontWeight: 800, color: 'var(--text-primary)' }}>{s.value}</div>
            <div style={{ fontSize: '0.65rem', color: 'var(--text-muted)' }}>{s.label}</div>
          </div>
        ))}
      </div>

      {/* Health Score Bar */}
      <div>
        <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
          <span style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>Community Health Score</span>
          <span style={{ fontSize: '0.72rem', fontWeight: 700, color: healthColor }}>{healthScore}/100</span>
        </div>
        <div style={{ height: '5px', borderRadius: '999px', background: 'var(--border)' }}>
          <div style={{ height: '100%', borderRadius: '999px', background: healthColor, width: `${healthScore}%`, transition: 'width 1s ease' }} />
        </div>
      </div>

      {d.activeEmergencies > 0 && (
        <div style={{ marginTop: '10px', background: 'rgba(239,68,68,0.1)', color: '#ef4444', fontSize: '0.75rem', fontWeight: 700, padding: '6px 10px', borderRadius: '6px', textAlign: 'center' }}>
          🚨 {d.activeEmergencies} Active Emergency Alert{d.activeEmergencies > 1 ? 's' : ''}
        </div>
      )}
    </div>
  );
}

// ─── Role Distribution chart ──────────────────────────────────────────────────
function RoleBar({ role, count, total, color }) {
  const pct = total > 0 ? Math.round((count / total) * 100) : 0;
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '12px', marginBottom: '12px' }}>
      <div style={{ width: '90px', fontSize: '0.8rem', color: 'var(--text-secondary)', fontWeight: 600 }}>{role}</div>
      <div style={{ flex: 1, height: '10px', borderRadius: '999px', background: 'var(--border)' }}>
        <div style={{ height: '100%', borderRadius: '999px', background: color, width: `${pct}%`, transition: 'width 0.8s ease' }} />
      </div>
      <div style={{ width: '40px', textAlign: 'right', fontSize: '0.82rem', fontWeight: 800, color }}>{count}</div>
    </div>
  );
}

// ─── Main Component ────────────────────────────────────────────────────────────
export default function SuperAdminDashboard() {
  const navigate = useNavigate();
  const [communities, setCommunities] = useState([]);
  const [users, setUsers] = useState([]);
  const [dashboards, setDashboards] = useState({});
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    Promise.all([
      communityApi.getAll().catch(() => ({ data: [] })),
      userApi.getAll().catch(() => ({ data: [] }))
    ]).then(async ([comRes, userRes]) => {
      const comList = comRes.data || [];
      const userList = userRes.data || [];
      setCommunities(comList);
      setUsers(userList);

      // Load dashboard for each community
      const dashMap = {};
      await Promise.allSettled(
        comList.map(async (c) => {
          const cid = c.communityId || c.id;
          try {
            const res = await dashboardApi.getCommunityDashboard(cid);
            dashMap[cid] = res.data;
          } catch (e) { dashMap[cid] = null; }
        })
      );
      setDashboards(dashMap);
    }).finally(() => setLoading(false));
  }, []);

  if (loading) return <DashboardLayout><LoadingSpinner /></DashboardLayout>;

  // Platform-level aggregates
  const totalResidents = Object.values(dashboards).reduce((s, d) => s + (d?.totalResidents || 0), 0);
  const totalPendingComplaints = Object.values(dashboards).reduce((s, d) => s + (d?.pendingComplaints || 0), 0);
  const totalActiveEmergencies = Object.values(dashboards).reduce((s, d) => s + (d?.activeEmergencies || 0), 0);
  const totalFlats = Object.values(dashboards).reduce((s, d) => s + (d?.totalFlats || 0), 0);
  const totalVehicles = Object.values(dashboards).reduce((s, d) => s + (d?.totalVehicles || 0), 0);
  const totalVisitors = Object.values(dashboards).reduce((s, d) => s + (d?.totalVisitors || 0), 0);

  // Role counts
  const roleCount = (roleName) => users.filter(u =>
    (u.roleName || u.role?.roleName || '').replace('ROLE_', '').toUpperCase() === roleName
  ).length;

  const roleData = [
    { role: 'Super Admin', count: roleCount('SUPER_ADMIN'), color: '#ef4444' },
    { role: 'Admin', count: roleCount('ADMIN'), color: '#6366f1' },
    { role: 'Security', count: roleCount('SECURITY'), color: '#3b82f6' },
    { role: 'Staff', count: roleCount('STAFF'), color: '#f59e0b' },
    { role: 'Resident', count: roleCount('RESIDENT'), color: '#10b981' },
  ];

  return (
    <DashboardLayout>
      {/* ─── Header ─────────────────────────────────────────────────────── */}
      <div style={{ marginBottom: '28px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
          <div>
            <h1 style={{ fontSize: '1.6rem', fontWeight: 900, margin: 0, color: 'var(--text-primary)' }}>
              🛡️ Platform Control Center
            </h1>
            <p style={{ color: 'var(--text-muted)', marginTop: '4px', fontSize: '0.88rem' }}>
              Super Admin · {communities.length} Registered {communities.length === 1 ? 'Community' : 'Communities'} · {users.length} Platform Users
            </p>
          </div>
          <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '8px', padding: '6px 12px' }}>
            📅 {new Date().toLocaleDateString('en-IN', { weekday: 'short', day: 'numeric', month: 'short', year: 'numeric' })}
          </div>
        </div>

        {totalActiveEmergencies > 0 && (
          <div style={{ marginTop: '14px', background: 'rgba(239,68,68,0.12)', border: '1px solid rgba(239,68,68,0.4)', borderRadius: '10px', padding: '10px 16px', display: 'flex', alignItems: 'center', gap: '10px' }}>
            <span style={{ fontSize: '1.2rem' }}>🚨</span>
            <span style={{ color: '#ef4444', fontWeight: 700, fontSize: '0.9rem' }}>
              Platform Alert: {totalActiveEmergencies} Active Emergency Alert{totalActiveEmergencies > 1 ? 's' : ''} across communities!
            </span>
          </div>
        )}
      </div>

      {/* ─── Platform KPIs ───────────────────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(6, 1fr)', gap: '14px', marginBottom: '28px' }}>
        <PlatformKpi icon="🏘️" label="Communities" value={communities.length} color="#6366f1" />
        <PlatformKpi icon="👥" label="Total Users" value={users.length} color="#3b82f6" />
        <PlatformKpi icon="🏠" label="Total Flats" value={totalFlats} color="#10b981" />
        <PlatformKpi icon="👤" label="Residents" value={totalResidents} color="#14b8a6" />
        <PlatformKpi icon="🔧" label="Pending Issues" value={totalPendingComplaints}
          color={totalPendingComplaints > 10 ? '#ef4444' : '#f59e0b'} />
        <PlatformKpi icon="🚨" label="Emergencies" value={totalActiveEmergencies}
          color={totalActiveEmergencies > 0 ? '#ef4444' : '#6b7280'}
          sub={totalActiveEmergencies === 0 ? 'All clear ✅' : 'Needs attention!'} />
      </div>

      {/* ─── Community Health Cards + Role Distribution ──────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: '1fr 320px', gap: '24px', marginBottom: '24px' }}>

        {/* Community Grid */}
        <div>
          <h3 style={{ fontSize: '1rem', fontWeight: 700, marginBottom: '14px', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '8px' }}>
            🏘️ Community Health Overview
            <span style={{ fontSize: '0.75rem', fontWeight: 400, color: 'var(--text-muted)' }}>Click to view details</span>
          </h3>
          {communities.length === 0 ? (
            <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '14px', padding: '40px', textAlign: 'center', color: 'var(--text-muted)' }}>
              No communities registered yet.
              <br />
              <button className="btn btn-primary" style={{ marginTop: '16px' }} onClick={() => navigate('/super-admin/communities')}>
                + Register Community
              </button>
            </div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: communities.length === 1 ? '1fr' : '1fr 1fr', gap: '16px' }}>
              {communities.map(c => {
                const cid = c.communityId || c.id;
                return (
                  <CommunityHealthCard
                    key={cid}
                    community={c}
                    dashboard={dashboards[cid]}
                    onClick={() => navigate('/super-admin/communities')}
                  />
                );
              })}
            </div>
          )}
        </div>

        {/* Right Column: Role Distribution + Quick Actions */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>

          {/* Role Distribution */}
          <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '14px', padding: '20px' }}>
            <h3 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '18px', color: 'var(--text-primary)' }}>
              👥 User Role Distribution
            </h3>
            {roleData.map(r => (
              <RoleBar key={r.role} role={r.role} count={r.count} total={users.length} color={r.color} />
            ))}
            <div style={{ marginTop: '14px', padding: '10px', background: 'var(--bg-main)', borderRadius: '8px', textAlign: 'center', fontSize: '0.78rem', color: 'var(--text-muted)' }}>
              {users.length} Total Platform Users
            </div>
          </div>

          {/* Admin Quick Actions */}
          <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '14px', padding: '20px' }}>
            <h3 style={{ fontSize: '0.95rem', fontWeight: 700, marginBottom: '14px', color: 'var(--text-primary)' }}>
              ⚡ Quick Actions
            </h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: '8px' }}>
              {[
                { label: '🏘️ Manage Communities', path: '/super-admin/communities', color: '#6366f1' },
                { label: '👥 Provision Users', path: '/super-admin/users', color: '#3b82f6' },
                { label: '🛡️ Role Management', path: '/super-admin/roles', color: '#f59e0b' },
              ].map(a => (
                <button
                  key={a.path}
                  onClick={() => navigate(a.path)}
                  style={{
                    background: `${a.color}18`,
                    border: `1px solid ${a.color}33`,
                    borderRadius: '8px',
                    padding: '10px 14px',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '10px',
                    cursor: 'pointer',
                    color: 'var(--text-primary)',
                    fontSize: '0.85rem',
                    fontWeight: 600,
                    transition: 'all 0.15s',
                    textAlign: 'left',
                    width: '100%'
                  }}
                  onMouseEnter={e => { e.currentTarget.style.background = `${a.color}28`; }}
                  onMouseLeave={e => { e.currentTarget.style.background = `${a.color}18`; }}
                >
                  {a.label}
                </button>
              ))}
            </div>
          </div>
        </div>
      </div>

      {/* ─── Platform Summary Footer ─────────────────────────────────────── */}
      <div style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '14px', padding: '18px 24px' }}>
        <h3 style={{ fontSize: '0.9rem', fontWeight: 700, marginBottom: '14px', color: 'var(--text-primary)' }}>
          📊 Platform-Wide Totals
        </h3>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(6, 1fr)', gap: '12px' }}>
          {[
            { label: 'Registered Vehicles', value: totalVehicles, icon: '🚗', color: '#3b82f6' },
            { label: 'Total Visitors', value: totalVisitors, icon: '👁️', color: '#8b5cf6' },
            { label: 'Total Complaints', value: Object.values(dashboards).reduce((s, d) => s + (d?.totalComplaints || 0), 0), icon: '🔧', color: '#f59e0b' },
            { label: 'Total Events', value: Object.values(dashboards).reduce((s, d) => s + (d?.totalEvents || 0), 0), icon: '🎉', color: '#10b981' },
            { label: 'Lost & Found', value: Object.values(dashboards).reduce((s, d) => s + (d?.totalLostFoundItems || 0), 0), icon: '🔍', color: '#f97316' },
            { label: 'Notices Posted', value: Object.values(dashboards).reduce((s, d) => s + (d?.totalNotices || 0), 0), icon: '📢', color: '#14b8a6' },
          ].map(s => (
            <div key={s.label} style={{ textAlign: 'center', padding: '12px', background: 'var(--bg-main)', borderRadius: '10px' }}>
              <div style={{ fontSize: '1.3rem' }}>{s.icon}</div>
              <div style={{ fontSize: '1.5rem', fontWeight: 800, color: s.color }}>{s.value}</div>
              <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', marginTop: '2px' }}>{s.label}</div>
            </div>
          ))}
        </div>
      </div>
    </DashboardLayout>
  );
}
