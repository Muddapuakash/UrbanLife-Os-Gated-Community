import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import { emergencyApi } from '../../../api/emergencyApi';
import { formatDate } from '../../../utils/formatDate';

const REFRESH_INTERVAL = 8000; // 8 seconds

export default function EmergencyListPage() {
  const [emergencies, setEmergencies] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  const loadEmergencies = useCallback((showLoader = false) => {
    if (showLoader) setLoading(true);
    emergencyApi.getByCommunity(1)
      .then(res => {
        setEmergencies(res.data);
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
        alert('Emergency Acknowledged by Gate Desk!');
        loadEmergencies();
      })
      .catch(err => alert('Failed to acknowledge'));
  };

  const handleResolve = (id) => {
    const notes = prompt('Enter resolution notes (required):');
    // If user cancelled the prompt
    if (notes === null) return;
    const finalNotes = notes.trim() || 'Resolved by security team';
    emergencyApi.resolve(id, finalNotes)
      .then(() => {
        alert('Emergency Marked Resolved!');
        loadEmergencies();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to resolve');
      });
  };

  const handleStart = (id) => {
    emergencyApi.start(id)
      .then(() => {
        alert('Response Started — Emergency is now IN_PROGRESS!');
        loadEmergencies();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to start response');
      });
  };

  const columns = [
    { header: 'ID', render: (r) => r.emergencyId || r.id },
    { header: 'Type', render: (r) => <Badge text={r.emergencyType} /> },
    { header: 'Priority', render: (r) => <Badge text={r.priority} /> },
    { header: 'Location', accessorKey: 'locationDetails' },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    { header: 'Reported At', render: (r) => formatDate(r.createdAt || r.reportedAt) },
    {
      header: 'Security Actions',
      render: (r) => {
        const id = r.emergencyId || r.id;
        const status = r.status;
        return (
          <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
            {(status === 'REPORTED' || status === 'OPEN') && (
              <button className="btn btn-warning" style={{ padding: '4px 10px', fontSize: '0.75rem' }} onClick={() => handleAcknowledge(id)}>
                1. Acknowledge
              </button>
            )}
            {status === 'ACKNOWLEDGED' && (
              <button className="btn btn-secondary" style={{ padding: '4px 10px', fontSize: '0.75rem' }} onClick={() => handleStart(id)}>
                2. Start Response
              </button>
            )}
            {status === 'IN_PROGRESS' && (
              <button className="btn btn-primary" style={{ padding: '4px 10px', fontSize: '0.75rem' }} onClick={() => handleResolve(id)}>
                3. Mark Resolved
              </button>
            )}
            {(status === 'RESOLVED' || status === 'CLOSED') && (
              <span style={{ color: 'var(--success)', fontSize: '0.75rem', fontWeight: 600 }}>✅ Resolved</span>
            )}
          </div>
        );
      }
    }
  ];

  return (
    <DashboardLayout>
      <PageHeader title="Emergency Response Desk" subtitle="Monitor active resident SOS alerts and broadcast updates" />
      
      <div style={{ marginBottom: '16px', padding: '12px 16px', background: 'rgba(59,130,246,0.1)', borderRadius: '8px', fontSize: '0.85rem', color: 'var(--text-muted)', borderLeft: '3px solid var(--info)' }}>
        <strong style={{ color: 'var(--info)' }}>Emergency Response Flow:</strong> &nbsp;
        REPORTED → <strong>Acknowledge</strong> → ACKNOWLEDGED → <strong>Start Response</strong> → IN_PROGRESS → <strong>Mark Resolved</strong> → RESOLVED
      </div>

      {loading ? <LoadingSpinner /> : <Table columns={columns} data={emergencies} emptyMessage="No active emergencies" />}
    </DashboardLayout>
  );
}
