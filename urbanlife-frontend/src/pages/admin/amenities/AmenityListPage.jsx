import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { amenityApi } from '../../../api/amenityApi';
import { AmenityType } from '../../../constants/enums';

export default function AmenityListPage() {
  const [amenities, setAmenities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    communityId: 1,
    name: '',
    amenityType: 'CLUBHOUSE',
    description: '',
    capacity: 50,
    openingTime: '06:00:00',
    closingTime: '22:00:00',
    maxBookingHours: 4
  });

  const loadAmenities = () => {
    setLoading(true);
    amenityApi.getByCommunity(1)
      .then(res => setAmenities(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadAmenities();
  }, []);

  const handleCreate = (e) => {
    e.preventDefault();

    const formattedOpening = formData.openingTime.length === 5 ? `${formData.openingTime}:00` : formData.openingTime;
    const formattedClosing = formData.closingTime.length === 5 ? `${formData.closingTime}:00` : formData.closingTime;

    const payload = {
      communityId: 1,
      name: formData.name,
      amenityType: formData.amenityType,
      description: formData.description || 'Society Amenity',
      capacity: Number(formData.capacity),
      openingTime: formattedOpening,
      closingTime: formattedClosing,
      maxBookingHours: Number(formData.maxBookingHours)
    };

    amenityApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        setFormData({
          communityId: 1,
          name: '',
          amenityType: 'CLUBHOUSE',
          description: '',
          capacity: 50,
          openingTime: '06:00:00',
          closingTime: '22:00:00',
          maxBookingHours: 4
        });
        alert('Amenity created successfully!');
        loadAmenities();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to create amenity.');
      });
  };

  const columns = [
    { header: 'ID', render: (r) => r.amenityId || r.id },
    { header: 'Amenity Name', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.name || r.amenityName}</strong> },
    { header: 'Type', accessorKey: 'amenityType' },
    { header: 'Timings', render: (r) => `${r.openingTime} - ${r.closingTime}` },
    { header: 'Max Hours', render: (r) => `${r.maxBookingHours || 4} hrs` },
    { header: 'Status', render: (r) => <Badge text={r.status || 'ACTIVE'} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader 
        title="Society Amenities" 
        subtitle="Manage community facilities available for resident slot booking"
        action={<button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>+ Add New Amenity</button>}
      />

      {loading ? <LoadingSpinner /> : <Table columns={columns} data={amenities} emptyMessage="No amenities configured yet" />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Society Amenity">
        <form onSubmit={handleCreate}>
          <FormInput 
            label="Amenity Name" 
            name="name" 
            value={formData.name} 
            onChange={e => setFormData({ ...formData, name: e.target.value })} 
            placeholder="e.g. Grand Clubhouse Hall" 
            required 
          />
          <FormInput 
            label="Amenity Type" 
            type="select" 
            options={AmenityType} 
            name="amenityType" 
            value={formData.amenityType} 
            onChange={e => setFormData({ ...formData, amenityType: e.target.value })} 
            required 
          />
          <FormInput 
            label="Opening Time" 
            type="time" 
            name="openingTime" 
            value={formData.openingTime.slice(0, 5)} 
            onChange={e => setFormData({ ...formData, openingTime: e.target.value })} 
            required 
          />
          <FormInput 
            label="Closing Time" 
            type="time" 
            name="closingTime" 
            value={formData.closingTime.slice(0, 5)} 
            onChange={e => setFormData({ ...formData, closingTime: e.target.value })} 
            required 
          />
          <FormInput 
            label="Max Booking Limit (Hours)" 
            type="number" 
            name="maxBookingHours" 
            value={formData.maxBookingHours} 
            onChange={e => setFormData({ ...formData, maxBookingHours: e.target.value })} 
            min="1" 
            required 
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Create Amenity
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
