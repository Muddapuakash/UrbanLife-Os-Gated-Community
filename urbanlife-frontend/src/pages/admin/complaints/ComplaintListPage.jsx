import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { complaintApi } from '../../../api/complaintApi';
import { userApi } from '../../../api/userApi';
import { formatDate } from '../../../utils/formatDate';

export default function ComplaintListPage() {
  const [complaints, setComplaints] = useState([]);
  const [staffUsers, setStaffUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [selectedComplaint, setSelectedComplaint] = useState(null);
  const [selectedStaffId, setSelectedStaffId] = useState('');
  const [isModalOpen, setIsModalOpen] = useState(false);

  const loadData = () => {
    setLoading(true);
    Promise.allSettled([
      complaintApi.getByCommunity(1),
      userApi.getAll()
    ]).then(([complaintsRes, usersRes]) => {
      if (complaintsRes.status === 'fulfilled') {
        setComplaints(complaintsRes.value.data);
      }
      if (usersRes.status === 'fulfilled') {
        const staff = (usersRes.value.data || []).filter(u =>
          u.roleName === 'STAFF' || u.role?.roleName === 'STAFF' || u.roleName === 'SECURITY' || u.role?.roleName === 'SECURITY' || u.roleName === 'ADMIN'
        );
        setStaffUsers(staff.length > 0 ? staff : usersRes.value.data || []);
        if (staff.length > 0) {
          setSelectedStaffId(staff[0].userId || staff[0].id);
        }
      }
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const openAssignModal = (complaint) => {
    setSelectedComplaint(complaint);
    setIsModalOpen(true);
  };

  const handleAssign = (e) => {
    e.preventDefault();
    if (!selectedComplaint || !selectedStaffId) return;

    const complaintId = selectedComplaint.complaintId || selectedComplaint.id;
    complaintApi.assign(complaintId, Number(selectedStaffId))
      .then(() => {
        alert('✅ Ticket assigned to staff member successfully!');
        setIsModalOpen(false);
        loadData();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to assign ticket.');
      });
  };

  const staffOptions = staffUsers.map(u => ({
    label: `${u.firstName || ''} ${u.lastName || ''} (${u.email || u.username}) — [${u.roleName || u.role?.roleName || 'STAFF'}]`.trim(),
    value: u.userId || u.id
  }));

  const columns = [
    { header: 'Ticket #', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.complaintId || r.id}</strong> },
    { header: 'Title', accessorKey: 'title' },
    { header: 'Category', accessorKey: 'category' },
    { header: 'Resident', render: (r) => r.residentName || 'N/A' },
    { header: 'Flat', render: (r) => r.flatNumber || 'N/A' },
    { header: 'Priority', render: (r) => <Badge text={r.priority} /> },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    { header: 'Assigned To', render: (r) => r.assignedToName ? <strong style={{ color: 'var(--info)' }}>{r.assignedToName}</strong> : <em style={{ color: 'var(--text-muted)' }}>Unassigned</em> },
    { header: 'Logged Date', render: (r) => formatDate(r.createdAt) },
    {
      header: 'Actions',
      render: (r) => (
        <button
          className="btn btn-primary"
          style={{ padding: '4px 10px', fontSize: '0.75rem' }}
          onClick={() => openAssignModal(r)}
        >
          {r.assignedToName ? 'Reassign Staff' : '👤 Assign Staff'}
        </button>
      )
    }
  ];

  return (
    <DashboardLayout>
      <PageHeader title="Complaint Tickets & Service Desk" subtitle="Assign maintenance tickets to electrical/plumbing staff and track resolution" />
      {loading ? <LoadingSpinner /> : <Table columns={columns} data={complaints} emptyMessage="No complaints logged yet" />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Assign Maintenance Ticket to Staff">
        <form onSubmit={handleAssign}>
          <div style={{ marginBottom: '12px', padding: '12px', background: 'rgba(59,130,246,0.1)', borderRadius: '8px', fontSize: '0.85rem' }}>
            <strong>Ticket:</strong> {selectedComplaint?.title}<br />
            <strong>Category:</strong> {selectedComplaint?.category} &nbsp;|&nbsp; <strong>Priority:</strong> {selectedComplaint?.priority}
          </div>
          <FormInput
            label="Select Staff Member"
            type="select"
            options={staffOptions}
            name="staffId"
            value={selectedStaffId}
            onChange={e => setSelectedStaffId(e.target.value)}
            required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '14px' }}>
            Confirm Ticket Assignment
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
