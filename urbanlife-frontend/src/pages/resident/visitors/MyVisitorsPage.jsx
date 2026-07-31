import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { visitorApi } from '../../../api/visitorApi';
import { residentApi } from '../../../api/residentApi';
import { formatDate } from '../../../utils/formatDate';
import { VisitorType } from '../../../constants/enums';
import { ShieldAlert } from 'lucide-react';

export default function MyVisitorsPage() {
  const [visitors, setVisitors] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [resolvedResidentId, setResolvedResidentId] = useState(null);
  const [notMapped, setNotMapped] = useState(false);
  
  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    visitorName: '',
    phone: '',
    visitorType: 'GUEST',
    expectedArrival: '',
    validUntil: ''
  });

  const loadVisitors = (resId) => {
    setLoading(true);
    visitorApi.getByResident(resId)
      .then(res => setVisitors(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    residentApi.getByUserId(currentUserId)
      .then(res => {
        const resId = res.data.residentId || res.data.id;
        setResolvedResidentId(resId);
        setNotMapped(false);
        loadVisitors(resId);
      })
      .catch(() => {
        setNotMapped(true);
        setLoading(false);
      });
  }, [currentUserId]);

  const handleCreate = (e) => {
    e.preventDefault();

    if (!resolvedResidentId) {
      alert('Your user account is not mapped to a flat in Resident Directory. Please contact Society Admin.');
      return;
    }

    const arrivalDate = new Date(formData.expectedArrival);
    const isoExpectedArrival = arrivalDate.toISOString().slice(0, 19);

    let isoValidUntil = '';
    if (formData.validUntil) {
      isoValidUntil = new Date(formData.validUntil).toISOString().slice(0, 19);
    } else {
      const validDate = new Date(arrivalDate.getTime() + 12 * 60 * 60 * 1000);
      isoValidUntil = validDate.toISOString().slice(0, 19);
    }

    const payload = {
      residentId: resolvedResidentId,
      visitorName: formData.visitorName,
      phone: formData.phone,
      visitorType: formData.visitorType,
      approvalType: 'PRE_APPROVED',
      expectedArrival: isoExpectedArrival,
      validUntil: isoValidUntil
    };

    visitorApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        setFormData({
          visitorName: '',
          phone: '',
          visitorType: 'GUEST',
          expectedArrival: '',
          validUntil: ''
        });
        alert('Visitor access pass generated successfully!');
        loadVisitors(resolvedResidentId);
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to pre-approve visitor.');
      });
  };

  const copyToClipboard = (text) => {
    navigator.clipboard.writeText(text).then(() => alert(`Copied: ${text}`));
  };

  const columns = [
    {
      header: 'Pass Code (Share with Visitor)',
      render: (r) => r.passCode ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <strong style={{ color: 'var(--primary)', fontFamily: 'monospace', fontSize: '0.95rem', letterSpacing: '1px' }}>
            {r.passCode}
          </strong>
          <button
            onClick={() => copyToClipboard(r.passCode)}
            style={{ background: 'none', border: '1px solid var(--border)', borderRadius: '4px', padding: '2px 6px', cursor: 'pointer', fontSize: '0.7rem', color: 'var(--text-muted)' }}
            title="Copy pass code"
          >
            📋 Copy
          </button>
        </div>
      ) : <span style={{ color: 'var(--text-muted)' }}>Generating...</span>
    },
    { header: 'Visitor Name', render: (r) => r.visitorName || r.name },
    { header: 'Phone Number', render: (r) => r.phone || r.phoneNumber },
    { header: 'Type', accessorKey: 'visitorType' },
    { header: 'Expected Arrival', render: (r) => formatDate(r.expectedArrival) },
    { header: 'Status', render: (r) => <Badge text={r.status} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader 
        title="Visitor Passes" 
        subtitle="Pre-approve guests and service providers for quick gate check-in"
        action={<button className="btn btn-primary" onClick={() => setIsModalOpen(true)} disabled={notMapped}>+ Pre-Approve Visitor</button>}
      />

      {notMapped && (
        <div style={{ marginBottom: '20px', padding: '16px', background: 'rgba(245, 158, 11, 0.12)', borderLeft: '4px solid var(--warning)', borderRadius: '8px', display: 'flex', gap: '12px', alignItems: 'center' }}>
          <ShieldAlert size={24} color="var(--warning)" />
          <div>
            <strong style={{ color: 'var(--warning)', display: 'block', marginBottom: '2px' }}>Resident Profile Pending Mapping</strong>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Your account has not been mapped to a flat by Society Admin yet. Ask your Society Admin to map your user under <strong>Admin &gt; Resident Directory</strong>.
            </span>
          </div>
        </div>
      )}

      {loading ? <LoadingSpinner /> : <Table columns={columns} data={visitors} emptyMessage="No visitor passes found" />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Pre-Approve Visitor Pass">
        <form onSubmit={handleCreate}>
          <FormInput 
            label="Visitor Name" 
            name="visitorName" 
            value={formData.visitorName} 
            onChange={e => setFormData({ ...formData, visitorName: e.target.value })} 
            placeholder="e.g. Akash"
            required 
          />
          <FormInput 
            label="Phone Number (10 digits starting 6-9)" 
            name="phone" 
            value={formData.phone} 
            onChange={e => setFormData({ ...formData, phone: e.target.value })} 
            placeholder="e.g. 9876543210"
            required 
          />
          <FormInput 
            label="Visitor Type" 
            type="select" 
            options={VisitorType} 
            name="visitorType" 
            value={formData.visitorType} 
            onChange={e => setFormData({ ...formData, visitorType: e.target.value })} 
            required 
          />
          <FormInput 
            label="Expected Arrival" 
            type="datetime-local" 
            name="expectedArrival" 
            value={formData.expectedArrival} 
            onChange={e => setFormData({ ...formData, expectedArrival: e.target.value })} 
            required 
          />
          <FormInput 
            label="Valid Until (Optional - Defaults to 12 Hours)" 
            type="datetime-local" 
            name="validUntil" 
            value={formData.validUntil} 
            onChange={e => setFormData({ ...formData, validUntil: e.target.value })} 
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Generate Access Pass
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
