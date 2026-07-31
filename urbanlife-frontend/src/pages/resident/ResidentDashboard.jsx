import { useEffect, useState, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import DashboardLayout from '../../components/layout/DashboardLayout';
import PageHeader from '../../components/common/PageHeader';
import Badge from '../../components/common/Badge';
import { useAuth } from '../../auth/AuthContext';
import { residentApi } from '../../api/residentApi';
import { visitorApi } from '../../api/visitorApi';
import { parcelApi } from '../../api/parcelApi';
import { complaintApi } from '../../api/complaintApi';
import { maintenanceBillApi } from '../../api/maintenanceBillApi';
import { noticeApi } from '../../api/noticeApi';
import { eventApi } from '../../api/eventApi';
import { pollApi } from '../../api/pollApi';
import { formatDate } from '../../utils/formatDate';

const REFRESH_INTERVAL = 10000; // 10 seconds

function StatCard({ icon, label, value, unit, loading, color, onClick, pulse }) {
  return (
    <div
      className="stat-card"
      onClick={onClick}
      style={{
        cursor: onClick ? 'pointer' : 'default',
        transition: 'transform 0.2s, box-shadow 0.2s',
        position: 'relative',
        overflow: 'hidden',
      }}
      onMouseEnter={e => { if (onClick) e.currentTarget.style.transform = 'translateY(-2px)'; }}
      onMouseLeave={e => { e.currentTarget.style.transform = 'translateY(0)'; }}
    >
      {pulse && (
        <span style={{
          position: 'absolute', top: '10px', right: '10px',
          width: '8px', height: '8px', borderRadius: '50%',
          background: 'var(--success)',
          boxShadow: '0 0 0 2px rgba(16,185,129,0.3)',
          animation: 'pulse 2s infinite'
        }} />
      )}
      <div className="stat-icon">{icon}</div>
      <div className="stat-info">
        <h4>{label}</h4>
        <div className="stat-value" style={{ color: color || undefined }}>
          {loading ? (
            <span style={{ fontSize: '1rem', opacity: 0.5 }}>···</span>
          ) : (
            <>{unit && <span style={{ fontSize: '1rem', fontWeight: 600 }}>{unit}</span>}{value}</>
          )}
        </div>
      </div>
    </div>
  );
}

function QuickActionBtn({ icon, label, to, navigate }) {
  return (
    <button
      onClick={() => navigate(to)}
      style={{
        display: 'flex', flexDirection: 'column', alignItems: 'center',
        gap: '8px', padding: '14px 16px',
        background: 'var(--surface)', border: '1px solid var(--border)',
        borderRadius: '12px', cursor: 'pointer', transition: 'all 0.2s',
        color: 'var(--text)', fontSize: '0.82rem', fontWeight: 600,
        minWidth: '100px', flex: '1',
      }}
      onMouseEnter={e => {
        e.currentTarget.style.background = 'var(--primary-light)';
        e.currentTarget.style.borderColor = 'var(--primary)';
        e.currentTarget.style.color = 'var(--primary)';
        e.currentTarget.style.transform = 'translateY(-2px)';
      }}
      onMouseLeave={e => {
        e.currentTarget.style.background = 'var(--surface)';
        e.currentTarget.style.borderColor = 'var(--border)';
        e.currentTarget.style.color = 'var(--text)';
        e.currentTarget.style.transform = 'translateY(0)';
      }}
    >
      <span style={{ fontSize: '1.5rem' }}>{icon}</span>
      {label}
    </button>
  );
}

export default function ResidentDashboard() {
  const { user } = useAuth();
  const navigate = useNavigate();

  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [residentInfo, setResidentInfo] = useState(null);

  const [notices, setNotices] = useState([]);
  const [events, setEvents] = useState([]);
  const [polls, setPolls] = useState([]);

  const [stats, setStats] = useState({
    expectedGuests: 0,
    pendingDeliveries: 0,
    activeComplaints: 0,
    maintenanceDue: 0,
  });

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  // ─── Resolve resident record then load all stats & feeds ───────────────────
  const loadStats = useCallback((resident) => {
    const residentId = resident?.residentId || resident?.id;
    const flatId = resident?.flatId;

    if (!residentId && !flatId) return;

    Promise.allSettled([
      // 1. Visitors
      residentId ? visitorApi.getByResident(residentId) : visitorApi.getByFlat(flatId),
      // 2. Parcels
      residentId ? parcelApi.getByResident(residentId) : parcelApi.getByFlat(flatId),
      // 3. Complaints
      residentId ? complaintApi.getByResident(residentId) : Promise.resolve({ data: [] }),
      // 4. Bills
      flatId ? maintenanceBillApi.getByFlat(flatId) : Promise.resolve({ data: [] }),
      // 5. Notices
      noticeApi.getByCommunity(1),
      // 6. Events
      eventApi.getByCommunity(1),
      // 7. Polls
      pollApi.getByCommunity(1)
    ]).then(([visitorsRes, parcelsRes, complaintsRes, billsRes, noticesRes, eventsRes, pollsRes]) => {

      // Visitors
      const visitors = visitorsRes.status === 'fulfilled' ? (visitorsRes.value.data || []) : [];
      const expectedGuests = visitors.filter(v => ['APPROVED', 'PENDING'].includes(v.visitStatus || v.status)).length;

      // Parcels
      const parcels = parcelsRes.status === 'fulfilled' ? (parcelsRes.value.data || []) : [];
      const pendingDeliveries = parcels.filter(p => ['RECEIVED', 'NOTIFIED', 'PENDING'].includes(p.status)).length;

      // Complaints
      const complaints = complaintsRes.status === 'fulfilled' ? (complaintsRes.value.data || []) : [];
      const activeComplaints = complaints.filter(c => !['RESOLVED', 'CLOSED', 'CANCELLED'].includes(c.status)).length;

      // Bills
      const bills = billsRes.status === 'fulfilled' ? (billsRes.value.data || []) : [];
      const maintenanceDue = bills
        .filter(b => !['PAID', 'CLEARED', 'CANCELLED'].includes(b.status))
        .reduce((sum, b) => sum + Number(b.amount || 0), 0);

      setStats({ expectedGuests, pendingDeliveries, activeComplaints, maintenanceDue });

      // Feeds
      if (noticesRes.status === 'fulfilled') setNotices(noticesRes.value.data || []);
      if (eventsRes.status === 'fulfilled') setEvents(eventsRes.value.data || []);
      if (pollsRes.status === 'fulfilled') setPolls(pollsRes.value.data || []);

      setLastUpdated(new Date());
      setLoading(false);
    });
  }, []);

  useEffect(() => {
    let interval;

    residentApi.getByUserId(currentUserId)
      .then(res => {
        setResidentInfo(res.data);
        loadStats(res.data);
        interval = setInterval(() => loadStats(res.data), REFRESH_INTERVAL);
      })
      .catch(() => {
        const email = localStorage.getItem('email') || '';
        residentApi.getByCommunity(1)
          .then(res => {
            const found = (res.data || []).find(r => (r.email || '').toLowerCase() === email.toLowerCase());
            setResidentInfo(found || null);
            loadStats(found || null);
            interval = setInterval(() => loadStats(found || null), REFRESH_INTERVAL);
          })
          .catch(() => setLoading(false));
      });

    return () => { if (interval) clearInterval(interval); };
  }, [currentUserId, loadStats]);

  const residentName = residentInfo?.residentName || user?.email?.split('@')[0] || 'Resident';

  return (
    <DashboardLayout>
      {/* ── Header ─────────────────────────────────────── */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' }}>
        <div>
          <h2 style={{ fontSize: '1.6rem', fontWeight: 800, marginBottom: '4px' }}>
            Resident Home Portal
          </h2>
          <p style={{ color: 'var(--text-muted)', fontSize: '0.88rem' }}>
            Welcome back, <strong style={{ color: 'var(--primary)' }}>{residentName}</strong>
            {residentInfo?.flatNumber && (
              <span style={{ marginLeft: '8px', fontSize: '0.8rem', background: 'var(--primary-light)', color: 'var(--primary)', padding: '2px 8px', borderRadius: '20px', fontWeight: 600 }}>
                Flat {residentInfo.flatNumber}
              </span>
            )}
          </p>
        </div>
        <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '5px', paddingTop: '6px', whiteSpace: 'nowrap' }}>
          <span style={{
            width: '7px', height: '7px', borderRadius: '50%',
            background: loading ? 'var(--warning)' : 'var(--success)',
            display: 'inline-block',
            animation: 'pulse 2s infinite'
          }} />
          {loading ? 'Loading...' : `Live · ${lastUpdated?.toLocaleTimeString('en-IN')}`}
        </div>
      </div>

      {/* ── Stat Cards ─────────────────────────────────── */}
      <div className="stats-grid" style={{ marginTop: '20px' }}>
        <StatCard icon="🔑" label="Expected Guests" value={stats.expectedGuests} loading={loading} pulse onClick={() => navigate('/resident/visitors')} />
        <StatCard icon="📦" label="Pending Deliveries" value={stats.pendingDeliveries} loading={loading} color={stats.pendingDeliveries > 0 ? 'var(--warning)' : undefined} pulse onClick={() => navigate('/resident/parcels')} />
        <StatCard icon="🔧" label="Active Complaints" value={stats.activeComplaints} loading={loading} color={stats.activeComplaints > 0 ? 'var(--danger)' : undefined} pulse onClick={() => navigate('/resident/complaints')} />
        <StatCard icon="💳" label="Maintenance Due" value={stats.maintenanceDue > 0 ? stats.maintenanceDue.toLocaleString('en-IN') : '0'} unit={stats.maintenanceDue > 0 ? '₹' : ''} loading={loading} color={stats.maintenanceDue > 0 ? 'var(--danger)' : 'var(--success)'} pulse onClick={() => navigate('/resident/bills')} />
      </div>

      {/* ── Quick Actions ──────────────────────────────── */}
      <div className="card" style={{ marginTop: '20px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '14px' }}>
          <h3 style={{ fontWeight: 700 }}>Quick Actions</h3>
          <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>Tap to navigate</span>
        </div>
        <div style={{ display: 'flex', gap: '10px', flexWrap: 'wrap' }}>
          <QuickActionBtn icon="📢" label="Notice Board" to="/resident/notices" navigate={navigate} />
          <QuickActionBtn icon="🎉" label="Events & RSVP" to="/resident/events" navigate={navigate} />
          <QuickActionBtn icon="📊" label="Polls & Voting" to="/resident/polls" navigate={navigate} />
          <QuickActionBtn icon="🔑" label="Visitor Pass" to="/resident/visitors" navigate={navigate} />
          <QuickActionBtn icon="📦" label="Deliveries" to="/resident/parcels" navigate={navigate} />
          <QuickActionBtn icon="🔧" label="Complaints" to="/resident/complaints" navigate={navigate} />
          <QuickActionBtn icon="💳" label="Pay Bills" to="/resident/bills" navigate={navigate} />
          <QuickActionBtn icon="🚗" label="My Vehicles" to="/resident/vehicles" navigate={navigate} />
          <QuickActionBtn icon="🏊" label="Amenities" to="/resident/amenities" navigate={navigate} />
          <QuickActionBtn icon="🚨" label="SOS Alert" to="/resident/emergencies" navigate={navigate} />
        </div>
      </div>

      {/* ── Live Notices & Events Grid ──────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(320px, 1fr))', gap: '16px', marginTop: '20px' }}>
        
        {/* Notice Bulletins */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
            <h3 style={{ fontWeight: 700, fontSize: '1.05rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              📢 Official Notices
            </h3>
            <button
              onClick={() => navigate('/resident/notices')}
              style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600 }}
            >
              View All →
            </button>
          </div>

          {notices.length === 0 ? (
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No official notices published yet.</p>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
              {notices.slice(0, 3).map(n => (
                <div key={n.noticeId || n.id} style={{ padding: '10px 12px', background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '8px' }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '4px' }}>
                    <strong style={{ fontSize: '0.9rem', color: 'var(--text)' }}>{n.title}</strong>
                    <Badge text={n.priority} />
                  </div>
                  <p style={{ fontSize: '0.82rem', color: 'var(--text-muted)', margin: '4px 0 6px 0', lineHeight: '1.4' }}>
                    {(n.message || '').slice(0, 90)}{n.message?.length > 90 ? '...' : ''}
                  </p>
                  <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)' }}>
                    Posted {formatDate(n.createdAt)}
                  </div>
                </div>
              ))}
            </div>
          )}
        </div>

        {/* Upcoming Events & Polls */}
        <div className="card">
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '12px' }}>
            <h3 style={{ fontWeight: 700, fontSize: '1.05rem', display: 'flex', alignItems: 'center', gap: '8px' }}>
              🎉 Events & 📊 Decision Polls
            </h3>
            <button
              onClick={() => navigate('/resident/events')}
              style={{ background: 'none', border: 'none', color: 'var(--primary)', cursor: 'pointer', fontSize: '0.8rem', fontWeight: 600 }}
            >
              View All →
            </button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
            {/* Events */}
            {events.slice(0, 2).map(e => (
              <div key={e.eventId || e.id} style={{ padding: '10px 12px', background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '8px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <strong style={{ fontSize: '0.88rem', color: 'var(--text)' }}>🎉 {e.title}</strong>
                  <span style={{ fontSize: '0.72rem', color: 'var(--primary)', fontWeight: 600 }}>{e.category}</span>
                </div>
                <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                  📍 {e.venue || 'Clubhouse'} &nbsp;|&nbsp; 🕒 {formatDate(e.startTime)}
                </div>
              </div>
            ))}

            {/* Polls */}
            {polls.slice(0, 2).map(p => (
              <div key={p.pollId || p.id} style={{ padding: '10px 12px', background: 'var(--surface)', border: '1px solid var(--border)', borderRadius: '8px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                  <strong style={{ fontSize: '0.88rem', color: 'var(--text)' }}>📊 {p.question}</strong>
                  <Badge text={p.status || 'ACTIVE'} />
                </div>
                <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                  Options: {(p.options || []).map(o => o.optionText || o.text || o).join(' / ')}
                </div>
              </div>
            ))}

            {events.length === 0 && polls.length === 0 && (
              <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>No active events or decision polls right now.</p>
            )}
          </div>
        </div>

      </div>

      <style>{`
        @keyframes pulse {
          0% { box-shadow: 0 0 0 0 rgba(16,185,129,0.4); }
          70% { box-shadow: 0 0 0 6px rgba(16,185,129,0); }
          100% { box-shadow: 0 0 0 0 rgba(16,185,129,0); }
        }
      `}</style>
    </DashboardLayout>
  );
}
