import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { parkingSlotApi } from '../../../api/parkingSlotApi';
import { parkingAllocationApi } from '../../../api/parkingAllocationApi';
import { vehicleApi } from '../../../api/vehicleApi';

export default function ParkingPage() {
  const [slots, setSlots] = useState([]);
  const [allocations, setAllocations] = useState([]);
  const [vehicles, setVehicles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // Slot creation modal
  const [showSlotModal, setShowSlotModal] = useState(false);
  const [slotError, setSlotError] = useState('');
  const [slotForm, setSlotForm] = useState({
    slotNumber: '',
    slotType: 'CAR',
    locationDescription: '',
    communityId: 1
  });

  // Allocation modal
  const [showAllocModal, setShowAllocModal] = useState(false);
  const [allocError, setAllocError] = useState('');
  const [allocForm, setAllocForm] = useState({
    vehicleId: '',
    parkingSlotId: '',
    startDate: new Date().toISOString().slice(0, 10)
  });

  // ─── Load all data ────────────────────────────────────────────────────────
  const loadData = () => {
    setLoading(true);
    Promise.all([
      parkingSlotApi.getByCommunity(1).catch(() => ({ data: [] })),
      parkingAllocationApi.getActive().catch(() => ({ data: [] })),
      vehicleApi.getByCommunity(1).catch(() => ({ data: [] }))
    ]).then(([slotRes, allocRes, vehicleRes]) => {
      const slotsList = slotRes.data || [];
      const allocsList = allocRes.data || [];
      const vehiclesList = vehicleRes.data || [];

      setSlots(slotsList);
      setAllocations(allocsList);
      setVehicles(vehiclesList);

      // Pre-select first available slot and first unallocated vehicle
      const availSlot = slotsList.find(s => s.status === 'AVAILABLE');
      const allocatedVehicleIds = new Set(allocsList.map(a => a.vehicleId));
      const freeVehicle = vehiclesList.find(v => !allocatedVehicleIds.has(v.vehicleId));

      setAllocForm(prev => ({
        ...prev,
        parkingSlotId: availSlot ? availSlot.parkingSlotId : '',
        vehicleId: freeVehicle ? freeVehicle.vehicleId : ''
      }));
    }).finally(() => setLoading(false));
  };

  useEffect(() => { loadData(); }, []);

  // ─── Create Parking Slot ──────────────────────────────────────────────────
  const handleCreateSlot = (e) => {
    e.preventDefault();
    setSaving(true);
    setSlotError('');
    parkingSlotApi.create(slotForm)
      .then(() => {
        setShowSlotModal(false);
        setSlotForm({ slotNumber: '', slotType: 'FOUR_WHEELER', locationDescription: '', communityId: 1 });
        loadData();
      })
      .catch(err => {
        const msg = err.response?.data?.message || err.response?.data?.error || 'Failed to create slot';
        setSlotError(msg);
      })
      .finally(() => setSaving(false));
  };

  // ─── Allocate Parking ──────────────────────────────────────────────────────
  const handleAllocate = (e) => {
    e.preventDefault();
    setSaving(true);
    setAllocError('');
    const payload = {
      vehicleId: Number(allocForm.vehicleId),
      parkingSlotId: Number(allocForm.parkingSlotId),
      startDate: allocForm.startDate
    };
    parkingAllocationApi.allocate(payload)
      .then(() => {
        setShowAllocModal(false);
        loadData();
      })
      .catch(err => {
        const msg = err.response?.data?.message || err.response?.data?.error || 'Failed to allocate slot';
        setAllocError(msg);
      })
      .finally(() => setSaving(false));
  };

  // ─── Release Allocation ───────────────────────────────────────────────────
  const handleRelease = (allocId) => {
    if (!window.confirm('Release this parking slot allocation?')) return;
    parkingAllocationApi.release(allocId)
      .then(() => loadData())
      .catch(err => alert(err.response?.data?.message || 'Failed to release slot'));
  };

  // ─── Table columns ────────────────────────────────────────────────────────
  const slotColumns = [
    { header: 'Slot No.', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.slotNumber}</strong> },
    { header: 'Location', render: (r) => r.locationDescription || '—' },
    { header: 'Type', render: (r) => <span style={{ textTransform: 'capitalize', fontSize: '0.85rem' }}>{(r.slotType || '').replace('_', ' ')}</span> },
    {
      header: 'Status',
      render: (r) => <Badge text={r.status || 'AVAILABLE'} variant={r.status === 'OCCUPIED' ? 'danger' : 'success'} />
    }
  ];

  const allocColumns = [
    { header: 'Slot No.', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.slotNumber}</strong> },
    { header: 'Vehicle No.', render: (r) => <strong style={{ color: 'var(--info)' }}>{r.vehicleNumber}</strong> },
    { header: 'Resident', render: (r) => r.residentName || `ID #${r.residentId}` },
    { header: 'Flat', render: (r) => r.flatNumber || '—' },
    { header: 'Start Date', render: (r) => r.startDate || '—' },
    { header: 'End Date', render: (r) => r.endDate || 'Ongoing' },
    {
      header: 'Action',
      render: (r) => (
        <button
          className="btn btn-secondary"
          style={{ fontSize: '0.75rem', padding: '4px 10px', color: 'var(--danger)', borderColor: 'var(--danger)' }}
          onClick={() => handleRelease(r.allocationId)}
        >
          Release
        </button>
      )
    }
  ];

  // Available slots & unallocated vehicles for dropdowns
  const availableSlots = slots.filter(s => s.status === 'AVAILABLE');
  const allocatedVehicleIds = new Set(allocations.map(a => a.vehicleId));
  const unallocatedVehicles = vehicles.filter(v => !allocatedVehicleIds.has(v.vehicleId));

  return (
    <DashboardLayout>
      <PageHeader
        title="Parking Slots & Allocations"
        subtitle="Manage society parking inventory and allocate slots to resident vehicles"
        action={
          <div style={{ display: 'flex', gap: '8px' }}>
            <button className="btn btn-secondary" onClick={() => { setShowSlotModal(true); setSlotError(''); }}>
              + Add Slot
            </button>
            <button className="btn btn-primary" onClick={() => { setShowAllocModal(true); setAllocError(''); }}
              disabled={availableSlots.length === 0 || unallocatedVehicles.length === 0}
              title={availableSlots.length === 0 ? 'No available slots' : unallocatedVehicles.length === 0 ? 'All vehicles already allocated' : ''}>
              + Allocate Slot
            </button>
          </div>
        }
      />

      {loading ? <LoadingSpinner /> : (
        <>
          {/* Stats row */}
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '16px', marginBottom: '28px' }}>
            {[
              { label: 'Total Slots', value: slots.length, color: 'var(--primary)' },
              { label: 'Available', value: availableSlots.length, color: 'var(--success)' },
              { label: 'Occupied', value: slots.filter(s => s.status === 'OCCUPIED').length, color: 'var(--danger)' }
            ].map(stat => (
              <div key={stat.label} style={{ background: 'var(--bg-card)', border: '1px solid var(--border)', borderRadius: '12px', padding: '16px 20px' }}>
                <div style={{ fontSize: '0.8rem', color: 'var(--text-muted)', marginBottom: '4px' }}>{stat.label}</div>
                <div style={{ fontSize: '1.8rem', fontWeight: 800, color: stat.color }}>{stat.value}</div>
              </div>
            ))}
          </div>

          {/* Active Allocations */}
          <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px' }}>🚗 Active Allocations</h3>
          <div style={{ marginBottom: '32px' }}>
            <Table columns={allocColumns} data={allocations} emptyMessage="No active parking allocations" />
          </div>

          {/* All Slots */}
          <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px' }}>🅿️ All Parking Slots</h3>
          <Table columns={slotColumns} data={slots} emptyMessage="No parking slots created yet — click '+ Add Slot' to begin" />
        </>
      )}

      {/* ─── Create Slot Modal ──────────────────────────────────────────── */}
      <Modal isOpen={showSlotModal} onClose={() => setShowSlotModal(false)} title="Create New Parking Slot">
        {slotError && (
          <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '12px' }}>
            {slotError}
          </div>
        )}
        <form onSubmit={handleCreateSlot}>
          <FormInput label="Slot Number *" placeholder="e.g. P-101, B2-05"
            value={slotForm.slotNumber}
            onChange={e => setSlotForm({ ...slotForm, slotNumber: e.target.value })} required
          />
          <FormInput label="Location Description" placeholder="e.g. Basement Level 1, Near Lift"
            value={slotForm.locationDescription}
            onChange={e => setSlotForm({ ...slotForm, locationDescription: e.target.value })}
          />
          <FormInput label="Slot Type *" type="select"
            options={[
              { label: '🚗 Car (Four Wheeler)', value: 'CAR' },
              { label: '🛵 Two Wheeler (Bike)', value: 'TWO_WHEELER' },
              { label: '⚡ EV Charging Slot', value: 'EV' },
              { label: '🧑‍🤝‍🧑 Visitor Parking', value: 'VISITOR' },
              { label: '🚲 Bicycle', value: 'BICYCLE' },
              { label: '📦 Other', value: 'OTHER' }
            ]}
            value={slotForm.slotType}
            onChange={e => setSlotForm({ ...slotForm, slotType: e.target.value })} required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? 'Creating...' : 'Create Slot'}
          </button>
        </form>
      </Modal>

      {/* ─── Allocate Slot Modal ────────────────────────────────────────── */}
      <Modal isOpen={showAllocModal} onClose={() => setShowAllocModal(false)} title="Allocate Parking Slot">
        {allocError && (
          <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '12px' }}>
            {allocError}
          </div>
        )}
        <form onSubmit={handleAllocate}>
          <FormInput label="Select Vehicle *" type="select"
            options={unallocatedVehicles.map(v => ({
              label: `${v.vehicleNumber} — ${v.vehicleType || ''} (${v.residentName || `Resident #${v.residentId}`})`,
              value: v.vehicleId
            }))}
            value={allocForm.vehicleId}
            onChange={e => setAllocForm({ ...allocForm, vehicleId: e.target.value })} required
          />
          <FormInput label="Select Available Slot *" type="select"
            options={availableSlots.map(s => ({
              label: `${s.slotNumber} — ${s.locationDescription || s.slotType}`,
              value: s.parkingSlotId
            }))}
            value={allocForm.parkingSlotId}
            onChange={e => setAllocForm({ ...allocForm, parkingSlotId: e.target.value })} required
          />
          <FormInput label="Start Date *" type="date"
            value={allocForm.startDate}
            onChange={e => setAllocForm({ ...allocForm, startDate: e.target.value })} required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? 'Allocating...' : 'Confirm Allocation'}
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
