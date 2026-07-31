import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { vehicleApi } from '../../../api/vehicleApi';
import { residentApi } from '../../../api/residentApi';
import { VehicleType } from '../../../constants/enums';
import { ShieldAlert } from 'lucide-react';

export default function MyVehiclesPage() {
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [resolvedResidentId, setResolvedResidentId] = useState(null);
  const [notMapped, setNotMapped] = useState(false);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    vehicleNumber: '',
    vehicleType: 'CAR',
    brand: '',
    model: '',
    color: ''
  });

  const loadVehicles = (resId) => {
    setLoading(true);
    vehicleApi.getByResident(resId)
      .then(res => setVehicles(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    residentApi.getByUserId(currentUserId)
      .then(res => {
        const resId = res.data.residentId || res.data.id;
        setResolvedResidentId(resId);
        setNotMapped(false);
        loadVehicles(resId);
      })
      .catch(() => {
        setNotMapped(true);
        setLoading(false);
      });
  }, [currentUserId]);

  const handleRegister = (e) => {
    e.preventDefault();

    if (!resolvedResidentId) {
      alert('Your user account is not mapped to a flat in Resident Directory. Please contact Society Admin.');
      return;
    }

    const payload = {
      residentId: resolvedResidentId,
      vehicleNumber: formData.vehicleNumber,
      vehicleType: formData.vehicleType,
      brand: formData.brand,
      model: formData.model,
      color: formData.color
    };

    vehicleApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        setFormData({
          vehicleNumber: '',
          vehicleType: 'CAR',
          brand: '',
          model: '',
          color: ''
        });
        alert('Vehicle registered successfully!');
        loadVehicles(resolvedResidentId);
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to register vehicle.');
      });
  };

  const columns = [
    { header: 'Vehicle Number', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.vehicleNumber || r.registrationNumber}</strong> },
    { header: 'Type', accessorKey: 'vehicleType' },
    { header: 'Brand & Model', render: (r) => `${r.brand || ''} ${r.model || ''}`.trim() || 'N/A' },
    { header: 'Color', accessorKey: 'color' },
    { header: 'Flat #', render: (r) => r.flatNumber || 'N/A' },
    { header: 'Status', render: (r) => <Badge text={r.active !== false ? 'ACTIVE' : 'INACTIVE'} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader 
        title="My Registered Vehicles" 
        subtitle="Manage authorized resident vehicles for automatic gate entry"
        action={<button className="btn btn-primary" onClick={() => setIsModalOpen(true)} disabled={notMapped}>+ Register Vehicle</button>}
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

      {loading ? <LoadingSpinner /> : <Table columns={columns} data={vehicles} emptyMessage="No registered vehicles found" />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register New Vehicle">
        <form onSubmit={handleRegister}>
          <FormInput 
            label="Vehicle Number" 
            name="vehicleNumber" 
            value={formData.vehicleNumber} 
            onChange={e => setFormData({ ...formData, vehicleNumber: e.target.value })} 
            placeholder="e.g. KA-01-AB-1234" 
            required 
          />
          <FormInput 
            label="Vehicle Type" 
            type="select" 
            options={VehicleType} 
            name="vehicleType" 
            value={formData.vehicleType} 
            onChange={e => setFormData({ ...formData, vehicleType: e.target.value })} 
            required 
          />
          <FormInput 
            label="Brand" 
            name="brand" 
            value={formData.brand} 
            onChange={e => setFormData({ ...formData, brand: e.target.value })} 
            placeholder="e.g. Honda" 
          />
          <FormInput 
            label="Model" 
            name="model" 
            value={formData.model} 
            onChange={e => setFormData({ ...formData, model: e.target.value })} 
            placeholder="e.g. City" 
          />
          <FormInput 
            label="Color" 
            name="color" 
            value={formData.color} 
            onChange={e => setFormData({ ...formData, color: e.target.value })} 
            placeholder="e.g. Red" 
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Register Vehicle
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
