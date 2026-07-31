import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { complaintApi } from '../../../api/complaintApi';
import { residentApi } from '../../../api/residentApi';
import { formatDate } from '../../../utils/formatDate';
import { ComplaintCategory, ComplaintPriority } from '../../../constants/enums';

const REFRESH_INTERVAL = 8000; // 8 seconds live refresh

export default function MyComplaintsPage() {
  const [complaints, setComplaints] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [resident, setResident] = useState(null);
  const [notMapped, setNotMapped] = useState(false);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'PLUMBING',
    priority: 'MEDIUM',
    residentId: '',
    flatId: '',
    communityId: 1
  });

  const loadComplaints = useCallback((resId, showLoader = false) => {
    if (showLoader) setLoading(true);
    if (!resId) {
      setComplaints([]);
      setLoading(false);
      return;
    }

    complaintApi.getByResident(resId)
      .then(res => {
        setComplaints(res.data || []);
        setLastUpdated(new Date());
      })
      .catch(err => console.error('Error fetching complaints:', err))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    let interval;

    // Step 1: Resolve resident profile by userId
    residentApi.getByUserId(currentUserId)
      .then(res => {
        const r = res.data;
        const resId = r?.residentId || r?.id;
        const fId = r?.flatId || r?.flat?.flatId;
        const cId = r?.communityId || 1;

        setResident(r);
        setFormData(prev => ({
          ...prev,
          residentId: resId || '',
          flatId: fId || '',
          communityId: cId
        }));

        loadComplaints(resId, true);
        interval = setInterval(() => loadComplaints(resId, false), REFRESH_INTERVAL);
      })
      .catch(() => {
        // Fallback: match by email in community list
        const email = localStorage.getItem('email') || '';
        residentApi.getByCommunity(1)
          .then(res => {
            const found = (res.data || []).find(
              r => (r.email || '').toLowerCase() === email.toLowerCase()
            );

            if (found) {
              const resId = found.residentId || found.id;
              const fId = found.flatId;
              const cId = found.communityId || 1;

              setResident(found);
              setFormData(prev => ({
                ...prev,
                residentId: resId || '',
                flatId: fId || '',
                communityId: cId
              }));

              loadComplaints(resId, true);
              interval = setInterval(() => loadComplaints(resId, false), REFRESH_INTERVAL);
            } else {
              setNotMapped(true);
              setLoading(false);
            }
          })
          .catch(() => {
            setNotMapped(true);
            setLoading(false);
          });
      });

    return () => { if (interval) clearInterval(interval); };
  }, [currentUserId, loadComplaints]);

  const handleCreate = (e) => {
    e.preventDefault();

    const payload = {
      ...formData,
      residentId: Number(formData.residentId || resident?.residentId || resident?.id || 1),
      flatId: Number(formData.flatId || resident?.flatId || 1),
      communityId: Number(formData.communityId || 1)
    };

    complaintApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        alert('✅ Service Ticket Raised Successfully!');
        setFormData(prev => ({ ...prev, title: '', description: '' }));
        loadComplaints(payload.residentId, false);
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to create complaint ticket');
      });
  };

  const columns = [
    {
      header: 'Ticket #',
      render: (r) => (
        <strong style={{ color: 'var(--primary)', fontFamily: 'monospace', fontSize: '0.95rem' }}>
          #{r.complaintId || r.ticketNumber || r.id}
        </strong>
      )
    },
    { header: 'Title', accessorKey: 'title' },
    { header: 'Category', accessorKey: 'category' },
    { header: 'Priority', render: (r) => <Badge text={r.priority} /> },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    { header: 'Logged On', render: (r) => formatDate(r.createdAt) }
  ];

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' }}>
        <PageHeader
          title="My Complaints & Tickets"
          subtitle="Raise service requests and track resolution status in real-time"
          action={
            <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
              + Raise Complaint
            </button>
          }
        />
        <div style={{
          fontSize: '0.75rem', color: 'var(--text-muted)',
          display: 'flex', alignItems: 'center', gap: '6px',
          paddingTop: '8px', whiteSpace: 'nowrap'
        }}>
          <span style={{
            width: '8px', height: '8px', borderRadius: '50%',
            background: 'var(--success)', display: 'inline-block'
          }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      {notMapped && (
        <div style={{
          marginBottom: '20px', padding: '14px 18px',
          background: 'rgba(239,68,68,0.1)', borderLeft: '4px solid var(--danger)', borderRadius: '8px'
        }}>
          <strong style={{ color: 'var(--danger)' }}>⚠️ Flat Assignment Required:</strong>
          <span style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginLeft: '8px' }}>
            Your account is not linked to a flat yet. Please ask your Society Admin to map your user to a flat in Admin → Resident Directory.
          </span>
        </div>
      )}

      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table
          columns={columns}
          data={complaints}
          emptyMessage="No complaint tickets raised yet. Click '+ Raise Complaint' above if you need maintenance assistance."
        />
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Raise Service Ticket">
        <form onSubmit={handleCreate}>
          <FormInput
            label="Complaint Title"
            name="title"
            value={formData.title}
            onChange={e => setFormData({ ...formData, title: e.target.value })}
            placeholder="e.g. Water leakage in bathroom"
            required
          />
          <FormInput
            label="Category"
            type="select"
            options={ComplaintCategory}
            name="category"
            value={formData.category}
            onChange={e => setFormData({ ...formData, category: e.target.value })}
            required
          />
          <FormInput
            label="Priority"
            type="select"
            options={ComplaintPriority}
            name="priority"
            value={formData.priority}
            onChange={e => setFormData({ ...formData, priority: e.target.value })}
            required
          />
          <FormInput
            label="Description"
            type="textarea"
            name="description"
            value={formData.description}
            onChange={e => setFormData({ ...formData, description: e.target.value })}
            placeholder="Provide detailed issue description..."
            required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Submit Complaint
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
