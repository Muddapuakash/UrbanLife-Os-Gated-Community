import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { parcelApi } from '../../../api/parcelApi';
import { residentApi } from '../../../api/residentApi';
import { DeliveryProvider, ParcelType } from '../../../constants/enums';
import { formatDate } from '../../../utils/formatDate';

const REFRESH_INTERVAL = 8000;

export default function ParcelListPage() {
  const [parcels, setParcels] = useState([]);
  const [residents, setResidents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isLogModalOpen, setIsLogModalOpen] = useState(false);
  const [lastUpdated, setLastUpdated] = useState(null);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    residentId: '',
    deliveryProvider: 'AMAZON',
    parcelType: 'PACKAGE',
    deliveryPersonName: '',
    description: ''
  });

  const loadParcels = useCallback((showLoader = false) => {
    if (showLoader) setLoading(true);
    parcelApi.getByCommunity(1)
      .then(res => {
        setParcels(res.data || []);
        setLastUpdated(new Date());
      })
      .catch(err => console.error('Parcel load error:', err))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    loadParcels(true);

    residentApi.getByCommunity(1)
      .then(res => {
        const list = res.data || [];
        setResidents(list);
        if (list.length > 0) {
          const firstId = list[0].residentId || list[0].id;
          setFormData(prev => ({ ...prev, residentId: firstId }));
        }
      })
      .catch(err => console.error(err));

    const interval = setInterval(() => loadParcels(), REFRESH_INTERVAL);
    return () => clearInterval(interval);
  }, [loadParcels]);

  const handleLogParcel = (e) => {
    e.preventDefault();
    const payload = {
      residentId: Number(formData.residentId),
      receivedByUserId: currentUserId,
      parcelType: formData.parcelType,
      deliveryProvider: formData.deliveryProvider,
      deliveryPersonName: formData.deliveryPersonName || 'Courier',
      description: formData.description || 'Parcel received at main gate'
    };

    parcelApi.create(payload)
      .then(res => {
        setIsLogModalOpen(false);
        const pickupCode = res.data?.pickupCode || res.data?.code;
        if (pickupCode) {
          alert(`✅ Parcel Logged!\n\n📦 Pickup Code: ${pickupCode}\n\nShare this code with the resident so they can collect their parcel.`);
        } else {
          alert('✅ Parcel logged successfully!');
        }
        loadParcels();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to log parcel.');
      });
  };

  const handleNotify = (id) => {
    parcelApi.notify(id)
      .then(() => {
        alert('📲 Notification sent to Resident!');
        loadParcels();
      })
      .catch(() => alert('Failed to notify resident.'));
  };

  const handleCollect = (id) => {
    const collectedByName = prompt('Enter name of person collecting this parcel:');
    if (!collectedByName || !collectedByName.trim()) return;

    parcelApi.collect(id, collectedByName.trim())
      .then(() => {
        alert('✅ Parcel marked as Collected!');
        loadParcels();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to collect parcel.');
      });
  };

  const residentOptions = residents.map(r => ({
    label: `${r.residentName || `${r.firstName || ''} ${r.lastName || ''}`.trim() || 'Resident'} — Flat ${r.flatNumber || 'N/A'}`,
    value: r.residentId || r.id
  }));

  const columns = [
    {
      header: 'Pickup Code',
      render: (r) => (
        <strong style={{ color: 'var(--primary)', fontFamily: 'monospace', fontSize: '1rem', letterSpacing: '1px' }}>
          {r.pickupCode || r.code || 'N/A'}
        </strong>
      )
    },
    { header: 'Provider', render: (r) => r.deliveryProvider || 'N/A' },
    { header: 'Type', render: (r) => r.parcelType || 'N/A' },
    { header: 'Resident', render: (r) => r.residentName || 'N/A' },
    { header: 'Flat', render: (r) => r.flatNumber || 'N/A' },
    { header: 'Logged At', render: (r) => formatDate(r.createdAt) },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    {
      header: 'Actions',
      render: (r) => {
        const id = r.parcelId || r.id;
        const status = r.status || '';

        if (status === 'COLLECTED' || status === 'RETURNED') return null;

        return (
          <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
            {(status === 'RECEIVED' || status === 'PENDING') && (
              <button
                className="btn btn-primary"
                style={{ padding: '4px 10px', fontSize: '0.72rem' }}
                onClick={() => handleNotify(id)}
              >
                📲 Notify
              </button>
            )}
            {(status === 'RECEIVED' || status === 'NOTIFIED' || status === 'PENDING') && (
              <button
                className="btn btn-success"
                style={{ padding: '4px 10px', fontSize: '0.72rem', background: 'var(--success)', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer' }}
                onClick={() => handleCollect(id)}
              >
                ✅ Collect
              </button>
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
          title="Delivery Package Desk"
          subtitle="Log and manage incoming Amazon, Flipkart & Swiggy parcels at the security gate"
          action={
            <button className="btn btn-primary" onClick={() => setIsLogModalOpen(true)}>
              + Log New Package
            </button>
          }
        />
        <div style={{
          fontSize: '0.75rem', color: 'var(--text-muted)',
          display: 'flex', alignItems: 'center', gap: '6px',
          paddingTop: '8px', whiteSpace: 'nowrap'
        }}>
          <span style={{ width: '8px', height: '8px', borderRadius: '50%', background: 'var(--success)', display: 'inline-block' }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      {loading ? <LoadingSpinner /> : (
        <Table columns={columns} data={parcels} emptyMessage="No parcels logged yet at this gate" />
      )}

      <Modal isOpen={isLogModalOpen} onClose={() => setIsLogModalOpen(false)} title="Log Incoming Delivery">
        <form onSubmit={handleLogParcel}>
          <FormInput
            label="Select Recipient Resident"
            type="select"
            options={residentOptions}
            name="residentId"
            value={formData.residentId}
            onChange={e => setFormData({ ...formData, residentId: e.target.value })}
            required
          />
          <FormInput
            label="Delivery Provider"
            type="select"
            options={DeliveryProvider}
            name="deliveryProvider"
            value={formData.deliveryProvider}
            onChange={e => setFormData({ ...formData, deliveryProvider: e.target.value })}
            required
          />
          <FormInput
            label="Package Type"
            type="select"
            options={ParcelType}
            name="parcelType"
            value={formData.parcelType}
            onChange={e => setFormData({ ...formData, parcelType: e.target.value })}
            required
          />
          <FormInput
            label="Delivery Person Name (Optional)"
            name="deliveryPersonName"
            value={formData.deliveryPersonName}
            onChange={e => setFormData({ ...formData, deliveryPersonName: e.target.value })}
            placeholder="e.g. Ravi Kumar"
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Save & Generate Pickup Code
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
