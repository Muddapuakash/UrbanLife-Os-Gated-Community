import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import { complaintApi } from '../../../api/complaintApi';
import { formatDate } from '../../../utils/formatDate';

const REFRESH_INTERVAL = 8000; // 8 seconds

export default function AssignedComplaintsPage() {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);

  // Get active staff user ID from localStorage
  const staffUserId = Number(localStorage.getItem('userId')) || 1;

  const loadComplaints = useCallback((showLoader = false) => {
    if (showLoader) setLoading(true);
    complaintApi.getByAssignedStaff(staffUserId)
      .then(res => {
        setComplaints(res.data || []);
        setLastUpdated(new Date());
      })
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, [staffUserId]);

  useEffect(() => {
    loadComplaints(true);
    const interval = setInterval(() => loadComplaints(false), REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [loadComplaints]);

  const handleUpdateStatus = (id, newStatus) => {
    const note = newStatus === 'RESOLVED' ? (prompt('Enter resolution notes:') || 'Issue fixed by staff') : null;
    complaintApi.updateStatus(id, newStatus, note)
      .then(() => {
        alert(`Ticket status updated to ${newStatus}`);
        loadComplaints(false);
      })
      .catch(err => alert(err.response?.data?.message || 'Failed to update status'));
  };

  const columns = [
    {
      header: 'ID',
      render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.complaintId || r.id}</strong>
    },
    { header: 'Title', accessorKey: 'title' },
    { header: 'Category', accessorKey: 'category' },
    { header: 'Flat', render: (r) => r.flatNumber || 'N/A' },
    { header: 'Priority', render: (r) => <Badge text={r.priority} /> },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    { header: 'Assigned On', render: (r) => formatDate(r.assignedAt || r.createdAt) },
    {
      header: 'Update Status',
      render: (r) => {
        const id = r.complaintId || r.id;
        return (
          <div style={{ display: 'flex', gap: '8px' }}>
            {(r.status === 'ASSIGNED' || r.status === 'OPEN') && (
              <button
                className="btn btn-secondary"
                style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                onClick={() => handleUpdateStatus(id, 'IN_PROGRESS')}
              >
                Start Work
              </button>
            )}
            {r.status === 'IN_PROGRESS' && (
              <button
                className="btn btn-primary"
                style={{ padding: '4px 10px', fontSize: '0.75rem' }}
                onClick={() => handleUpdateStatus(id, 'RESOLVED')}
              >
                Mark Resolved
              </button>
            )}
            {(r.status === 'RESOLVED' || r.status === 'CLOSED') && (
              <span style={{ color: 'var(--success)', fontWeight: 600, fontSize: '0.8rem' }}>
                ✓ Completed
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
          title="My Assigned Work Tickets"
          subtitle="Maintenance tasks assigned to you — update status as you progress"
        />
        <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', display: 'flex', alignItems: 'center', gap: '6px', paddingTop: '8px', whiteSpace: 'nowrap' }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', display: 'inline-block' }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      {loading ? <LoadingSpinner /> : (
        <Table
          columns={columns}
          data={complaints}
          emptyMessage="No maintenance tickets assigned to you at the moment"
        />
      )}
    </DashboardLayout>
  );
}
