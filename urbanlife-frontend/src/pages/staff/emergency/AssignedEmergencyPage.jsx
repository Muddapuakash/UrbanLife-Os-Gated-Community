import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import { emergencyApi } from '../../../api/emergencyApi';
import { formatDate } from '../../../utils/formatDate';
import { ShieldAlert } from 'lucide-react';

const REFRESH_INTERVAL = 8000; // 8 seconds

export default function AssignedEmergencyPage() {
  const [emergencies, setEmergencies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  const loadEmergencies = useCallback((showLoader = false) => {
    if (showLoader) setLoading(true);
    emergencyApi.getByCommunity(1)
      .then(res => {
        setEmergencies(res.data || []);
        setLastUpdated(new Date());
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    loadEmergencies(true);
    const interval = setInterval(() => loadEmergencies(false), REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [loadEmergencies]);

  const handleAcknowledge = (id) => {
    emergencyApi.acknowledge(id)
      .then(() => {
        alert('✅ Emergency Acknowledged!');
        loadEmergencies(false);
      })
      .catch(err => alert(err.response?.data?.message || 'Failed to acknowledge'));
  };

  const handleStart = (id) => {
    emergencyApi.start(id)
      .then(() => {
        alert('🚀 Emergency Response Started!');
        loadEmergencies(false);
      })
      .catch(err => alert(err.response?.data?.message || 'Failed to start response'));
  };

  const handleResolve = (id) => {
    const notes = prompt('Enter resolution notes (required):');
    if (notes === null) return;
    const finalNotes = notes.trim() || 'Resolved by emergency response team';

    emergencyApi.resolve(id, finalNotes)
      .then(() => {
        alert('✅ Emergency Marked Resolved!');
        loadEmergencies(false);
      })
      .catch(err => alert(err.response?.data?.message || 'Failed to resolve emergency'));
  };

  const columns = [
    { header: 'ID', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.emergencyId || r.id}</strong> },
    { header: 'Type', render: (r) => <Badge text={r.emergencyType} /> },
    { header: 'Location', accessorKey: 'locationDetails' },
    { header: 'Description', render: (r) => r.description || 'SOS Alert' },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    { header: 'Reported At', render: (r) => formatDate(r.createdAt || r.reportedAt) },
    {
      header: 'My Actions',
      render: (r) => {
        const id = r.emergencyId || r.id;
        const status = r.status;
        return (
          <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
            {(status === 'REPORTED' || status === 'OPEN') && (
              <button
                className="btn btn-warning"
                style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                onClick={() => handleAcknowledge(id)}
              >
                1. Acknowledge
              </button>
            )}
            {status === 'ACKNOWLEDGED' && (
              <button
                className="btn btn-secondary"
                style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                onClick={() => handleStart(id)}
              >
                2. Start Response
              </button>
            )}
            {status === 'IN_PROGRESS' && (
              <button
                className="btn btn-primary"
                style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                onClick={() => handleResolve(id)}
              >
                3. Mark Resolved
              </button>
            )}
            {(status === 'RESOLVED' || status === 'CLOSED') && (
              <span style={{ color: 'var(--success)', fontWeight: 600, fontSize: '0.8rem' }}>
                ✅ Resolved
              </span>
            )}
          </div>
        );
      }
    }
  ];

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' }}>
        <PageHeader
          title="Emergency Response Assignments"
          subtitle="Emergency situations assigned to you for immediate response"
        />
        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '6px', paddingTop: '8px', whiteSpace: 'nowrap' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', display: 'inline-block' }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      <div style={{ marginBottom: '20px', padding: '12px 16px', background: 'rgba(244,63,94,0.08)', borderLeft: '4px solid var(--danger)', borderRadius: '8px', display: 'flex', gap: '12px', alignItems: 'center' }}>
        <ShieldAlert size={20} color="var(--danger)" />
        <span style={{ fontSize: '0.85rem', color: 'var(--danger)', fontWeight: 600 }}>
          Emergency Flow: REPORTED → Acknowledge → ACKNOWLEDGED → Start Response → IN_PROGRESS → Mark Resolved → RESOLVED
        </span>
      </div>

      {loading ? <LoadingSpinner /> : (
        <Table
          columns={columns}
          data={emergencies}
          emptyMessage="No emergencies active at the moment"
        />
      )}
    </DashboardLayout>
  );
}
