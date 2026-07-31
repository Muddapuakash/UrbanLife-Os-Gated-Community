import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { residentApi } from '../../../api/residentApi';
import { userApi } from '../../../api/userApi';
import { flatApi } from '../../../api/flatApi';
import { ResidentType } from '../../../constants/enums';

export default function ResidentListPage() {
  const [residents, setResidents] = useState([]);
  const [users, setUsers] = useState([]);
  const [flats, setFlats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    userId: '',
    flatId: '',
    residentType: 'OWNER',
    primaryResident: true,
    moveInDate: new Date().toISOString().slice(0, 10),
    emergencyContactName: '',
    emergencyContactPhone: ''
  });

  const loadData = () => {
    setLoading(true);
    Promise.all([
      residentApi.getByCommunity(1).catch(() => ({ data: [] })),
      userApi.getAll().catch(() => ({ data: [] })),
      flatApi.getByCommunity(1).catch(() => ({ data: [] }))
    ]).then(([resData, userData, flatData]) => {
      setResidents(resData.data);
      setUsers(userData.data);
      setFlats(flatData.data);

      if (userData.data.length > 0 && !formData.userId) {
        setFormData(prev => ({ ...prev, userId: userData.data[0].userId || userData.data[0].id }));
      }
      if (flatData.data.length > 0 && !formData.flatId) {
        setFormData(prev => ({ ...prev, flatId: flatData.data[0].flatId || flatData.data[0].id }));
      }
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const handleCreate = (e) => {
    e.preventDefault();
    const payload = {
      userId: Number(formData.userId),
      flatId: Number(formData.flatId),
      residentType: formData.residentType,
      primaryResident: Boolean(formData.primaryResident),
      moveInDate: formData.moveInDate,
      emergencyContactName: formData.emergencyContactName || null,
      emergencyContactPhone: formData.emergencyContactPhone || null
    };

    residentApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        alert('Resident mapped to flat successfully!');
        loadData();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to map resident to flat.');
      });
  };

  const columns = [
    { header: 'Resident ID', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.residentId || r.id}</strong> },
    { header: 'Full Name', render: (r) => r.residentName || `${r.firstName || ''} ${r.lastName || ''}`.trim() || 'N/A' },
    { header: 'Flat #', render: (r) => r.flatNumber || 'N/A' },
    { header: 'Resident Type', render: (r) => <Badge text={r.residentType} /> },
    { header: 'Move-in Date', accessorKey: 'moveInDate' },
    { header: 'Status', render: (r) => <Badge text={r.status || (r.isActive !== false ? 'ACTIVE' : 'MOVED_OUT')} /> }
  ];

  const userOptions = users.map(u => ({
    label: `${u.firstName || ''} ${u.lastName || ''} (${u.email})`,
    value: u.userId || u.id
  }));

  const flatOptions = flats.map(f => ({
    label: `Flat ${f.flatNumber || f.number} (${f.blockName || 'Block'})`,
    value: f.flatId || f.id
  }));

  return (
    <DashboardLayout>
      <PageHeader 
        title="Resident Directory" 
        subtitle="Society registered residents and flat allocations"
        action={<button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>+ Map Resident to Flat</button>}
      />
      {loading ? <LoadingSpinner /> : <Table columns={columns} data={residents} />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Map Resident User to Flat">
        <form onSubmit={handleCreate}>
          <FormInput 
            label="Select Registered User" 
            type="select" 
            options={userOptions} 
            name="userId" 
            value={formData.userId} 
            onChange={e => setFormData({ ...formData, userId: e.target.value })} 
            required 
          />
          <FormInput 
            label="Select Flat" 
            type="select" 
            options={flatOptions} 
            name="flatId" 
            value={formData.flatId} 
            onChange={e => setFormData({ ...formData, flatId: e.target.value })} 
            required 
          />
          <FormInput 
            label="Resident Ownership Type" 
            type="select" 
            options={ResidentType} 
            name="residentType" 
            value={formData.residentType} 
            onChange={e => setFormData({ ...formData, residentType: e.target.value })} 
            required 
          />
          <FormInput 
            label="Move-in Date" 
            type="date" 
            name="moveInDate" 
            value={formData.moveInDate} 
            onChange={e => setFormData({ ...formData, moveInDate: e.target.value })} 
            required 
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Assign Resident to Flat
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
