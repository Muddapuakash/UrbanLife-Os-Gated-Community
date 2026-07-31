import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { amenityBookingApi } from '../../../api/amenityBookingApi';
import { amenityApi } from '../../../api/amenityApi';
import { residentApi } from '../../../api/residentApi';
import { formatDateOnly } from '../../../utils/formatDate';
import { ShieldAlert, Info } from 'lucide-react';

export default function BookAmenityPage() {
  const [bookings, setBookings] = useState([]);
  const [amenities, setAmenities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [resolvedResidentId, setResolvedResidentId] = useState(null);
  const [notMapped, setNotMapped] = useState(false);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const [formData, setFormData] = useState({
    amenityId: '',
    bookingDate: new Date().toISOString().slice(0, 10),
    startTime: '09:00',
    endTime: '11:00',
    numberOfPeople: 2,
    purpose: 'Personal Reservation'
  });

  const loadBookings = (resId) => {
    setLoading(true);
    amenityBookingApi.getByResident(resId)
      .then(res => setBookings(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    // Fetch amenities for dropdown
    amenityApi.getByCommunity(1)
      .then(res => {
        setAmenities(res.data);
        if (res.data.length > 0) {
          setFormData(prev => ({ ...prev, amenityId: res.data[0].amenityId || res.data[0].id }));
        }
      })
      .catch(err => console.error(err));

    // Resolve residentId
    residentApi.getByUserId(currentUserId)
      .then(res => {
        const resId = res.data.residentId || res.data.id;
        setResolvedResidentId(resId);
        setNotMapped(false);
        loadBookings(resId);
      })
      .catch(() => {
        setNotMapped(true);
        setLoading(false);
      });
  }, [currentUserId]);

  const handleBook = (e) => {
    e.preventDefault();

    if (!resolvedResidentId) {
      alert('Your user account is not mapped to a flat in Resident Directory. Please contact Society Admin.');
      return;
    }

    if (!formData.amenityId) {
      alert('No amenity selected. Please contact Society Admin to create society amenities first.');
      return;
    }

    // Format LocalTime strings to HH:mm:ss
    const formattedStartTime = formData.startTime.length === 5 ? `${formData.startTime}:00` : formData.startTime;
    const formattedEndTime = formData.endTime.length === 5 ? `${formData.endTime}:00` : formData.endTime;

    const payload = {
      amenityId: Number(formData.amenityId),
      residentId: resolvedResidentId,
      bookingDate: formData.bookingDate,
      startTime: formattedStartTime,
      endTime: formattedEndTime,
      numberOfPeople: Number(formData.numberOfPeople),
      purpose: formData.purpose
    };

    amenityBookingApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        alert('Amenity slot reserved successfully!');
        loadBookings(resolvedResidentId);
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to book amenity slot. Check time slot availability.');
      });
  };

  const columns = [
    { header: 'ID', render: (r) => r.bookingId || r.id },
    { header: 'Amenity', render: (r) => r.amenityName || 'Clubhouse' },
    { header: 'Booking Date', render: (r) => formatDateOnly(r.bookingDate) },
    { header: 'Time Slot', render: (r) => `${r.startTime} - ${r.endTime}` },
    { header: 'Guests', accessorKey: 'numberOfPeople' },
    { header: 'Status', render: (r) => <Badge text={r.status} /> }
  ];

  const amenityOptions = amenities.map(a => ({
    label: `${a.name || a.amenityName} (${a.locationDetails || 'Society'})`,
    value: a.amenityId || a.id
  }));

  return (
    <DashboardLayout>
      <PageHeader 
        title="Amenity Bookings" 
        subtitle="Reserve society Clubhouse, Gym, Swimming Pool & Sports courts"
        action={<button className="btn btn-primary" onClick={() => setIsModalOpen(true)} disabled={notMapped || amenities.length === 0}>+ Book Amenity</button>}
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

      {!notMapped && amenities.length === 0 && (
        <div style={{ marginBottom: '20px', padding: '16px', background: 'rgba(59, 130, 246, 0.12)', borderLeft: '4px solid var(--info)', borderRadius: '8px', display: 'flex', gap: '12px', alignItems: 'center' }}>
          <Info size={24} color="var(--info)" />
          <div>
            <strong style={{ color: 'var(--info)', display: 'block', marginBottom: '2px' }}>No Registered Amenities Found</strong>
            <span style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>
              There are no amenities (Clubhouse, Gym, Pool) configured for your society in the database yet.
            </span>
          </div>
        </div>
      )}

      {loading ? <LoadingSpinner /> : <Table columns={columns} data={bookings} emptyMessage="No amenity reservations found" />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Reserve Amenity Slot">
        <form onSubmit={handleBook}>
          {amenityOptions.length > 0 && (
            <FormInput 
              label="Select Amenity" 
              type="select" 
              options={amenityOptions} 
              name="amenityId" 
              value={formData.amenityId} 
              onChange={e => setFormData({ ...formData, amenityId: e.target.value })} 
              required 
            />
          )}

          <FormInput 
            label="Booking Date" 
            type="date" 
            name="bookingDate" 
            value={formData.bookingDate} 
            onChange={e => setFormData({ ...formData, bookingDate: e.target.value })} 
            required 
          />
          <FormInput 
            label="Start Time" 
            type="time" 
            name="startTime" 
            value={formData.startTime} 
            onChange={e => setFormData({ ...formData, startTime: e.target.value })} 
            required 
          />
          <FormInput 
            label="End Time" 
            type="time" 
            name="endTime" 
            value={formData.endTime} 
            onChange={e => setFormData({ ...formData, endTime: e.target.value })} 
            required 
          />
          <FormInput 
            label="Number of Guests / People" 
            type="number" 
            name="numberOfPeople" 
            value={formData.numberOfPeople} 
            onChange={e => setFormData({ ...formData, numberOfPeople: e.target.value })} 
            min="1" 
            required 
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Confirm Reservation
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
