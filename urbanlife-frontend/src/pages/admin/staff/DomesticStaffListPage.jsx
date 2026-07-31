import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { domesticStaffApi } from '../../../api/domesticStaffApi';
import { staffRatingApi } from '../../../api/staffRatingApi';
import { useAuth } from '../../../auth/AuthContext';

export default function DomesticStaffListPage() {
  const { user } = useAuth();
  const communityId = Number(user?.communityId || localStorage.getItem('communityId') || 1);

  const [staffList, setStaffList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // Modal states
  const [showAddModal, setShowAddModal] = useState(false);
  const [showRatingsModal, setShowRatingsModal] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [ratings, setRatings] = useState([]);

  const [form, setForm] = useState({
    name: '',
    phone: '',
    staffType: 'MAID',
    customStaffType: '',
    address: '',
    verificationReference: ''
  });

  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const loadData = () => {
    setLoading(true);
    domesticStaffApi.getByCommunity(communityId)
      .then(res => setStaffList(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => { loadData(); }, [communityId]);

  const handleAddStaff = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      await domesticStaffApi.create({
        ...form,
        communityId
      });
      setSuccess('Domestic staff registered successfully!');
      setTimeout(() => {
        setShowAddModal(false);
        setForm({ name: '', phone: '', staffType: 'MAID', customStaffType: '', address: '', verificationReference: '' });
        loadData();
      }, 1000);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to register staff.');
    } finally {
      setSaving(false);
    }
  };

  const handleVerify = async (staffId) => {
    try {
      await domesticStaffApi.verify(staffId, { verificationStatus: 'VERIFIED', remarks: 'Verified by Admin' });
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to verify staff.');
    }
  };

  const handleBlock = async (staffId) => {
    const reason = window.prompt('Enter reason for blocking staff:');
    if (!reason) return;
    try {
      await domesticStaffApi.block(staffId, { reason });
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to block staff.');
    }
  };

  const handleViewRatings = async (staff) => {
    setSelectedStaff(staff);
    setShowRatingsModal(true);
    try {
      const res = await staffRatingApi.getByStaff(staff.staffId || staff.id);
      setRatings(res.data || []);
    } catch (e) {
      setRatings([]);
    }
  };

  const columns = [
    { header: 'ID', render: (r) => `#${r.staffId || r.id}` },
    { header: 'Name', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.name}</strong> },
    { header: 'Phone', accessorKey: 'phone' },
    { header: 'Aadhaar / ID', render: (r) => {
      const ref = r.verificationReference;
      if (!ref) return <span style={{ color: 'var(--text-muted)' }}>Not provided</span>;
      const masked = ref.length > 4 ? `****-****-${ref.slice(-4)}` : ref;
      return <code style={{ background: 'var(--bg-main)', padding: '2px 6px', borderRadius: '4px', color: 'var(--warning)' }}>{masked}</code>;
    }},
    { header: 'Rating', render: (r) => r.averageRating > 0 ? `${'⭐'.repeat(Math.round(r.averageRating))} (${r.averageRating.toFixed(1)})` : '—' },
    { header: 'Role', render: (r) => <span style={{ textTransform: 'capitalize' }}>{(r.staffType || '').toLowerCase().replace('_', ' ')}</span> },
    {
      header: 'Verification',
      render: (r) => (
        <Badge
          text={r.verificationStatus || 'PENDING'}
          variant={r.verificationStatus === 'VERIFIED' ? 'success' : r.verificationStatus === 'REJECTED' ? 'danger' : 'warning'}
        />
      )
    },
    {
      header: 'Status',
      render: (r) => (
        <Badge
          text={r.status || 'ACTIVE'}
          variant={r.status === 'ACTIVE' ? 'success' : 'danger'}
        />
      )
    },
    {
      header: 'Actions',
      render: (r) => (
        <div style={{ display: 'flex', gap: '6px' }}>
          {r.verificationStatus !== 'VERIFIED' && (
            <button className="btn btn-secondary" style={{ fontSize: '0.72rem', padding: '3px 8px' }} onClick={() => handleVerify(r.staffId || r.id)}>
              ✅ Verify
            </button>
          )}
          {r.status === 'ACTIVE' && (
            <button className="btn btn-secondary" style={{ fontSize: '0.72rem', padding: '3px 8px', color: 'var(--danger)' }} onClick={() => handleBlock(r.staffId || r.id)}>
              🚫 Block
            </button>
          )}
          <button className="btn btn-secondary" style={{ fontSize: '0.72rem', padding: '3px 8px' }} onClick={() => handleViewRatings(r)}>
            ⭐ Ratings
          </button>
        </div>
      )
    }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Domestic Staff Management"
        subtitle="Register, verify, and monitor domestic helpers, cooks, drivers, and cleaners"
        action={
          <button className="btn btn-primary" onClick={() => { setShowAddModal(true); setError(''); setSuccess(''); }}>
            + Register Staff
          </button>
        }
      />

      {loading ? <LoadingSpinner /> : (
        <Table columns={columns} data={staffList} emptyMessage="No domestic staff registered yet" />
      )}

      {/* Add Staff Modal */}
      <Modal isOpen={showAddModal} onClose={() => setShowAddModal(false)} title="Register Domestic Helper / Staff">
        {error && <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px', borderRadius: '8px', marginBottom: '12px', fontSize: '0.85rem' }}>{error}</div>}
        {success && <div style={{ background: 'rgba(16,185,129,0.15)', color: 'var(--success)', padding: '10px', borderRadius: '8px', marginBottom: '12px', fontSize: '0.85rem' }}>{success}</div>}

        <form onSubmit={handleAddStaff}>
          <FormInput label="Full Name *" placeholder="e.g. Ramesh Kumar" value={form.name} onChange={e => setForm({ ...form, name: e.target.value })} required />
          <FormInput label="Phone Number *" placeholder="e.g. 9876543210" value={form.phone} onChange={e => setForm({ ...form, phone: e.target.value })} required />
          <FormInput label="Staff Role *" type="select"
            options={[
              { label: 'Maid / Housekeeping', value: 'MAID' },
              { label: 'Cook / Chef', value: 'COOK' },
              { label: 'Driver', value: 'DRIVER' },
              { label: 'Cleaner', value: 'CLEANER' },
              { label: 'Babysitter / Nanny', value: 'BABYSITTER' },
              { label: 'Electrician', value: 'ELECTRICIAN' },
              { label: 'Plumber', value: 'PLUMBER' },
              { label: 'Car Washer', value: 'CAR_WASHER' },
              { label: 'Gardener', value: 'GARDENER' },
              { label: 'Caregiver', value: 'CAREGIVER' },
              { label: 'Other', value: 'OTHER' }
            ]}
            value={form.staffType} onChange={e => setForm({ ...form, staffType: e.target.value })} required
          />
          <FormInput label="Verification Document Reference" placeholder="Aadhaar / ID Card Number" value={form.verificationReference} onChange={e => setForm({ ...form, verificationReference: e.target.value })} />
          <FormInput label="Address" placeholder="Local address" value={form.address} onChange={e => setForm({ ...form, address: e.target.value })} />

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? 'Registering...' : 'Register Staff'}
          </button>
        </form>
      </Modal>

      {/* Ratings Modal */}
      <Modal isOpen={showRatingsModal} onClose={() => setShowRatingsModal(false)} title={`Ratings & Reviews for ${selectedStaff?.name || ''}`}>
        <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
          {ratings.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '20px' }}>No ratings or reviews submitted for this staff yet.</p>
          ) : (
            ratings.map((r, i) => (
              <div key={i} style={{ background: 'var(--bg-main)', border: '1px solid var(--border)', borderRadius: '8px', padding: '12px', marginBottom: '10px' }}>
                <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '4px' }}>
                  <span style={{ fontWeight: 700, color: 'var(--warning)' }}>{'⭐'.repeat(r.rating || 5)}</span>
                  <span style={{ fontSize: '0.75rem', color: 'var(--text-muted)' }}>{r.createdAt ? new Date(r.createdAt).toLocaleDateString() : ''}</span>
                </div>
                <div style={{ fontSize: '0.85rem', color: 'var(--text-secondary)' }}>{r.review || 'No written review.'}</div>
                <div style={{ fontSize: '0.72rem', color: 'var(--text-muted)', marginTop: '4px' }}>By Resident ID #{r.residentId}</div>
              </div>
            ))
          )}
        </div>
      </Modal>
    </DashboardLayout>
  );
}
