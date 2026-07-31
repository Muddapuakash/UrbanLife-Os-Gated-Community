import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { communityApi } from '../../../api/communityApi';

export default function CommunityListPage() {
  const [communities, setCommunities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    name: '',
    registrationNumber: '',
    email: '',
    phone: '',
    addressLine: '',
    city: 'Hyderabad',
    state: 'Telangana',
    pincode: '500001'
  });

  const loadCommunities = () => {
    setLoading(true);
    communityApi.getAll()
      .then(res => setCommunities(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadCommunities();
  }, []);

  const handleCreateCommunity = (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      registrationNumber: formData.registrationNumber || `REG-${Date.now()}`
    };

    communityApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        alert('🏙️ Residential Community Registered Successfully!');
        setFormData(prev => ({ ...prev, name: '', email: '', phone: '', addressLine: '' }));
        loadCommunities();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to register community.');
      });
  };

  const columns = [
    { header: 'ID', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.communityId || r.id}</strong> },
    { header: 'Society Name', render: (r) => r.name || r.communityName },
    { header: 'Email', accessorKey: 'email' },
    { header: 'Phone', accessorKey: 'phone' },
    { header: 'City', accessorKey: 'city' },
    { header: 'State', accessorKey: 'state' },
    { header: 'Pincode', accessorKey: 'pincode' }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Community Directory"
        subtitle="Registered residential societies and gated apartment communities"
        action={
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            + Register New Community
          </button>
        }
      />
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table columns={columns} data={communities} emptyMessage="No residential communities registered yet." />
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Gated Society Community">
        <form onSubmit={handleCreateCommunity}>
          <FormInput
            label="Society / Community Name"
            name="name"
            value={formData.name}
            onChange={e => setFormData({ ...formData, name: e.target.value })}
            placeholder="e.g. Royal Palms Luxury Heights"
            required
          />
          <FormInput
            label="Registration / RERA Number (Optional)"
            name="registrationNumber"
            value={formData.registrationNumber}
            onChange={e => setFormData({ ...formData, registrationNumber: e.target.value })}
            placeholder="e.g. RERA-HYD-2026-99"
          />
          <FormInput
            label="Society Office Email"
            type="email"
            name="email"
            value={formData.email}
            onChange={e => setFormData({ ...formData, email: e.target.value })}
            placeholder="e.g. admin@royalpalms.com"
            required
          />
          <FormInput
            label="Contact Phone Number (10 digits)"
            type="tel"
            name="phone"
            value={formData.phone}
            onChange={e => setFormData({ ...formData, phone: e.target.value })}
            placeholder="e.g. 9876543210"
            required
          />
          <FormInput
            label="Street Address"
            name="addressLine"
            value={formData.addressLine}
            onChange={e => setFormData({ ...formData, addressLine: e.target.value })}
            placeholder="e.g. Plot 42, Financial District, Gachibowli"
            required
          />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: '8px' }}>
            <FormInput
              label="City"
              name="city"
              value={formData.city}
              onChange={e => setFormData({ ...formData, city: e.target.value })}
              required
            />
            <FormInput
              label="State"
              name="state"
              value={formData.state}
              onChange={e => setFormData({ ...formData, state: e.target.value })}
              required
            />
            <FormInput
              label="Pincode (6 digits)"
              name="pincode"
              value={formData.pincode}
              onChange={e => setFormData({ ...formData, pincode: e.target.value })}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Register Residential Community
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
