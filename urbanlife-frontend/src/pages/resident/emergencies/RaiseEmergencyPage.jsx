import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import FormInput from '../../../components/common/FormInput';
import { emergencyApi } from '../../../api/emergencyApi';
import { residentApi } from '../../../api/residentApi';
import { EmergencyType, EmergencyPriority } from '../../../constants/enums';
import { ShieldAlert } from 'lucide-react';

export default function RaiseEmergencyPage() {
  const [resolvedResidentId, setResolvedResidentId] = useState(null);
  const [notMapped, setNotMapped] = useState(false);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    emergencyType: 'MEDICAL',
    priority: 'HIGH',
    description: '',
    locationDetails: 'Flat A-101'
  });
  const [loading, setLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    residentApi.getByUserId(currentUserId)
      .then(res => {
        const resId = res.data.residentId || res.data.id;
        setResolvedResidentId(resId);
        setNotMapped(false);

        if (res.data.flatNumber && res.data.blockName) {
          setFormData(prev => ({
            ...prev,
            locationDetails: `${res.data.blockName} - Flat ${res.data.flatNumber}`
          }));
        }
      })
      .catch(() => {
        setNotMapped(true);
      });
  }, [currentUserId]);

  const handleRaise = (e) => {
    e.preventDefault();

    if (!resolvedResidentId) {
      alert('Your user account is not mapped to a flat in Resident Directory. Please contact Society Admin.');
      return;
    }

    setLoading(true);
    setSuccessMsg('');

    const payload = {
      residentId: resolvedResidentId,
      emergencyType: formData.emergencyType,
      priority: formData.priority || 'HIGH',
      description: formData.description,
      locationDetails: formData.locationDetails
    };

    emergencyApi.create(payload)
      .then(() => {
        setSuccessMsg(`🚨 SOS Alert Broadcasted! Gate security and community responders have been notified.`);
        setFormData(prev => ({ ...prev, description: '' }));
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to trigger SOS alert');
      })
      .finally(() => setLoading(false));
  };

  return (
    <DashboardLayout>
      <PageHeader title="Emergency SOS Desk" subtitle="Instant emergency broadcast to gate security & administration" />

      {notMapped && (
        <div style={{ marginBottom: '20px', padding: '16px', background: 'rgba(245, 158, 11, 0.12)', borderLeft: '4px solid var(--warning)', borderRadius: '8px', display: 'flex', gap: '12px', alignItems: 'center', maxWidth: '600px' }}>
          <ShieldAlert size={24} color="var(--warning)" />
          <div>
            <strong style={{ color: 'var(--warning)', display: 'block', marginBottom: '2px' }}>Resident Profile Pending Mapping</strong>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              Your account has not been mapped to a flat by Society Admin yet. Ask your Society Admin to map your user under <strong>Admin &gt; Resident Directory</strong>.
            </span>
          </div>
        </div>
      )}

      <div className="card" style={{ maxWidth: '600px', border: '1px solid var(--danger)' }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '12px', color: 'var(--danger)', marginBottom: '16px' }}>
          <ShieldAlert size={32} />
          <div>
            <h3 style={{ fontSize: '1.2rem', fontWeight: 800 }}>Trigger Immediate Emergency Alert</h3>
            <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Use this only during active emergency situations.</p>
          </div>
        </div>

        {successMsg && (
          <div style={{ background: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)', padding: '12px', borderRadius: '8px', marginBottom: '16px', fontWeight: 600 }}>
            {successMsg}
          </div>
        )}

        <form onSubmit={handleRaise}>
          <FormInput 
            label="Emergency Type" 
            type="select" 
            options={EmergencyType} 
            name="emergencyType" 
            value={formData.emergencyType} 
            onChange={e => setFormData({ ...formData, emergencyType: e.target.value })} 
            required 
          />
          <FormInput 
            label="Priority Level" 
            type="select" 
            options={EmergencyPriority} 
            name="priority" 
            value={formData.priority} 
            onChange={e => setFormData({ ...formData, priority: e.target.value })} 
            required 
          />
          <FormInput 
            label="Location Details" 
            name="locationDetails" 
            value={formData.locationDetails} 
            onChange={e => setFormData({ ...formData, locationDetails: e.target.value })} 
            required 
          />
          <FormInput 
            label="Situation Description" 
            type="textarea" 
            name="description" 
            value={formData.description} 
            onChange={e => setFormData({ ...formData, description: e.target.value })} 
            placeholder="Describe emergency details..." 
            required 
          />
          <button 
            type="submit" 
            className="btn btn-danger" 
            style={{ width: '100%', padding: '14px', fontSize: '1rem', marginTop: '12px' }} 
            disabled={loading || notMapped}
          >
            {loading ? 'Broadcasting SOS...' : '🚨 BROADCAST SOS ALERT NOW'}
          </button>
        </form>
      </div>
    </DashboardLayout>
  );
}
