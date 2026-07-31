import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { eventApi } from '../../../api/eventApi';
import { formatDate } from '../../../utils/formatDate';

export default function EventListPage() {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const userRole = (localStorage.getItem('role') || '').replace('ROLE_', '');
  const isAdmin = userRole === 'ADMIN' || userRole === 'SUPER_ADMIN';

  const [formData, setFormData] = useState({
    title: '',
    description: '',
    category: 'CULTURAL',
    venue: 'Club House Main Hall',
    startTime: new Date(Date.now() + 86400000).toISOString().slice(0, 16),
    endTime: new Date(Date.now() + 97200000).toISOString().slice(0, 16),
    maxParticipants: 100,
    communityId: 1
  });

  const loadEvents = () => {
    setLoading(true);
    eventApi.getByCommunity(1)
      .then(res => setEvents(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadEvents();
  }, []);

  const handleCreateEvent = (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      maxParticipants: Number(formData.maxParticipants),
      communityId: Number(formData.communityId || 1)
    };

    eventApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        alert('🎉 Community Event Created Successfully!');
        setFormData(prev => ({ ...prev, title: '', description: '' }));
        loadEvents();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to create event.');
      });
  };

  const columns = [
    { header: 'ID', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.eventId || r.id}</strong> },
    { header: 'Event Title', accessorKey: 'title' },
    { header: 'Category', accessorKey: 'category' },
    { header: 'Venue / Location', accessorKey: 'venue' },
    { header: 'Start Time', render: (r) => formatDate(r.startTime) },
    { header: 'Status', render: (r) => <Badge text={r.isCancelled ? 'CANCELLED' : 'ACTIVE'} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Community Events & RSVP"
        subtitle="Schedule social gatherings, festival celebrations, and general body meetings"
        action={
          isAdmin ? (
            <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
              + Create Event
            </button>
          ) : null
        }
      />
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table columns={columns} data={events} emptyMessage="No society events scheduled yet." />
      )}

      {isAdmin && (
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Schedule New Community Event">
          <form onSubmit={handleCreateEvent}>
            <FormInput
              label="Event Title"
              name="title"
              value={formData.title}
              onChange={e => setFormData({ ...formData, title: e.target.value })}
              placeholder="e.g. Annual General Body Meeting or Diwali Night"
              required
            />
            <FormInput
              label="Event Category"
              type="select"
              options={[
                { label: 'Cultural Program', value: 'CULTURAL' },
                { label: 'Sports & Games', value: 'SPORTS' },
                { label: 'General Body Meeting', value: 'MEETING' },
                { label: 'Festival Celebration', value: 'CELEBRATION' },
                { label: 'Maintenance Drive', value: 'MAINTENANCE' },
                { label: 'Social Gathering', value: 'SOCIAL' },
                { label: 'Other', value: 'OTHER' }
              ]}
              name="category"
              value={formData.category}
              onChange={e => setFormData({ ...formData, category: e.target.value })}
              required
            />
            <FormInput
              label="Venue / Location"
              name="venue"
              value={formData.venue}
              onChange={e => setFormData({ ...formData, venue: e.target.value })}
              placeholder="e.g. Clubhouse Hall A / Central Lawn"
              required
            />
            <FormInput
              label="Start Date & Time"
              type="datetime-local"
              name="startTime"
              value={formData.startTime}
              onChange={e => setFormData({ ...formData, startTime: e.target.value })}
              required
            />
            <FormInput
              label="End Date & Time"
              type="datetime-local"
              name="endTime"
              value={formData.endTime}
              onChange={e => setFormData({ ...formData, endTime: e.target.value })}
              required
            />
            <FormInput
              label="Max Participants Capacity"
              type="number"
              name="maxParticipants"
              value={formData.maxParticipants}
              onChange={e => setFormData({ ...formData, maxParticipants: e.target.value })}
              placeholder="e.g. 150"
              required
            />
            <FormInput
              label="Description"
              type="textarea"
              name="description"
              value={formData.description}
              onChange={e => setFormData({ ...formData, description: e.target.value })}
              placeholder="Describe event details, agenda, and instructions for residents..."
              required
            />
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Publish Event
            </button>
          </form>
        </Modal>
      )}
    </DashboardLayout>
  );
}
