import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { noticeApi } from '../../../api/noticeApi';
import { blockApi } from '../../../api/blockApi';
import { formatDate } from '../../../utils/formatDate';

export default function NoticeListPage() {
  const [notices, setNotices] = useState([]);
  const [blocks, setBlocks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const userRole = (localStorage.getItem('role') || '').replace('ROLE_', '');
  const isAdmin = userRole === 'ADMIN' || userRole === 'SUPER_ADMIN';

  const [formData, setFormData] = useState({
    title: '',
    message: '',
    noticeType: 'GENERAL',
    priority: 'NORMAL',
    targetType: 'COMMUNITY',
    blockId: '',
    communityId: 1
  });

  const loadNotices = () => {
    setLoading(true);
    noticeApi.getByCommunity(1)
      .then(res => setNotices(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadNotices();
    blockApi.getByCommunity(1)
      .then(res => {
        const list = res.data || [];
        setBlocks(list);
        if (list.length > 0) {
          const firstBlockId = list[0].blockId || list[0].id;
          setFormData(prev => ({ ...prev, blockId: firstBlockId }));
        }
      })
      .catch(err => console.error('Failed to load blocks:', err));
  }, []);

  const handleCreateNotice = (e) => {
    e.preventDefault();

    if (formData.targetType === 'BLOCK' && !formData.blockId) {
      alert('Please select a building block for Block-specific notice.');
      return;
    }

    const payload = {
      ...formData,
      communityId: Number(formData.communityId || 1),
      blockId: formData.targetType === 'BLOCK' ? Number(formData.blockId) : null
    };

    noticeApi.create(payload)
      .then((res) => {
        const noticeId = res.data?.noticeId || res.data?.id;
        if (noticeId) {
          noticeApi.publish(noticeId).catch(() => {});
        }
        setIsModalOpen(false);
        alert('📢 Notice Broadcasted Successfully!');
        setFormData(prev => ({ ...prev, title: '', message: '' }));
        loadNotices();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to publish notice.');
      });
  };

  const blockOptions = blocks.map(b => ({
    label: `${b.blockName || b.name || 'Block'} (${b.blockCode || 'Block Code'})`,
    value: b.blockId || b.id
  }));

  const columns = [
    { header: 'ID', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.noticeId || r.id}</strong> },
    { header: 'Title', accessorKey: 'title' },
    { header: 'Type', accessorKey: 'noticeType' },
    { header: 'Priority', render: (r) => <Badge text={r.priority} /> },
    {
      header: 'Target Scope',
      render: (r) => (
        <span style={{ fontSize: '0.85rem' }}>
          {r.targetType === 'BLOCK' ? `🏢 ${r.blockName || 'Specific Block'}` : '🌐 Entire Community'}
        </span>
      )
    },
    { header: 'Status', render: (r) => <Badge text={r.status || 'PUBLISHED'} /> },
    { header: 'Created Date', render: (r) => formatDate(r.createdAt) }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Community Notice Board"
        subtitle="Official broadcasts, bulletins, and announcements"
        action={
          isAdmin ? (
            <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
              + Broadcast Notice
            </button>
          ) : null
        }
      />
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table columns={columns} data={notices} emptyMessage="No official notices published yet." />
      )}

      {isAdmin && (
        <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Broadcast New Notice">
          <form onSubmit={handleCreateNotice}>
            <FormInput
              label="Notice Title"
              name="title"
              value={formData.title}
              onChange={e => setFormData({ ...formData, title: e.target.value })}
              placeholder="e.g. Scheduled Water Tank Cleaning"
              required
            />
            <FormInput
              label="Notice Type"
              type="select"
              options={[
                { label: 'General Announcement', value: 'GENERAL' },
                { label: 'Maintenance Work', value: 'MAINTENANCE' },
                { label: 'Water Supply Update', value: 'WATER_SUPPLY' },
                { label: 'Power & Electricity', value: 'POWER' },
                { label: 'Security Advisory', value: 'SECURITY' },
                { label: 'Parking Notice', value: 'PARKING' },
                { label: 'Society Event', value: 'EVENT' },
                { label: 'General Meeting', value: 'MEETING' },
                { label: 'Emergency Alert', value: 'EMERGENCY' },
                { label: 'Dues / Payment Reminder', value: 'PAYMENT_REMINDER' },
                { label: 'Other', value: 'OTHER' }
              ]}
              name="noticeType"
              value={formData.noticeType}
              onChange={e => setFormData({ ...formData, noticeType: e.target.value })}
              required
            />
            <FormInput
              label="Priority Level"
              type="select"
              options={[
                { label: 'Low', value: 'LOW' },
                { label: 'Normal', value: 'NORMAL' },
                { label: 'High', value: 'HIGH' },
                { label: 'Urgent', value: 'URGENT' }
              ]}
              name="priority"
              value={formData.priority}
              onChange={e => setFormData({ ...formData, priority: e.target.value })}
              required
            />
            <FormInput
              label="Target Audience Scope"
              type="select"
              options={[
                { label: 'Entire Community', value: 'COMMUNITY' },
                { label: 'Specific Block', value: 'BLOCK' }
              ]}
              name="targetType"
              value={formData.targetType}
              onChange={e => {
                const val = e.target.value;
                setFormData(prev => ({
                  ...prev,
                  targetType: val,
                  blockId: val === 'BLOCK' && blocks.length > 0 ? (blocks[0].blockId || blocks[0].id) : prev.blockId
                }));
              }}
              required
            />

            {formData.targetType === 'BLOCK' && (
              <FormInput
                label="Select Target Building Block"
                type="select"
                options={blockOptions}
                name="blockId"
                value={formData.blockId}
                onChange={e => setFormData({ ...formData, blockId: e.target.value })}
                required
              />
            )}

            <FormInput
              label="Notice Content / Message"
              type="textarea"
              name="message"
              value={formData.message}
              onChange={e => setFormData({ ...formData, message: e.target.value })}
              placeholder="Write full notice message details for residents..."
              required
            />
            <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
              Publish & Broadcast Notice
            </button>
          </form>
        </Modal>
      )}
    </DashboardLayout>
  );
}
