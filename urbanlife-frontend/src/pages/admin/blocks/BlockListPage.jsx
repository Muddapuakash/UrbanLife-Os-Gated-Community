import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import Badge from '../../../components/common/Badge';
import { blockApi } from '../../../api/blockApi';
import { communityApi } from '../../../api/communityApi';

export default function BlockListPage() {
  const [blocks, setBlocks] = useState([]);
  const [communities, setCommunities] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const [formData, setFormData] = useState({
    blockName: '',
    blockCode: '',
    totalFloors: 10,
    communityId: 1
  });

  const loadBlocks = () => {
    setLoading(true);
    blockApi.getAll()
      .then(res => setBlocks(res.data || []))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    loadBlocks();
    communityApi.getAll()
      .then(res => {
        setCommunities(res.data || []);
        if (res.data && res.data.length > 0) {
          const firstId = res.data[0].communityId || res.data[0].id;
          setFormData(prev => ({ ...prev, communityId: firstId }));
        }
      })
      .catch(err => console.error(err));
  }, []);

  const handleCreateBlock = (e) => {
    e.preventDefault();
    const payload = {
      ...formData,
      totalFloors: Number(formData.totalFloors),
      communityId: Number(formData.communityId)
    };

    blockApi.create(payload)
      .then(() => {
        setIsModalOpen(false);
        alert('🏢 Building Block Added Successfully!');
        setFormData(prev => ({ ...prev, blockName: '', blockCode: '' }));
        loadBlocks();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to add building block.');
      });
  };

  const communityOptions = communities.map(c => ({
    label: c.name || c.communityName || 'Society',
    value: c.communityId || c.id
  }));

  const columns = [
    { header: 'ID', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.blockId || r.id}</strong> },
    { header: 'Block Name', render: (r) => r.blockName || r.name },
    { header: 'Block Code', render: (r) => r.blockCode || 'N/A' },
    { header: 'Total Floors', accessorKey: 'totalFloors' },
    { header: 'Community', render: (r) => r.communityName || 'Green Valley Apartments' },
    { header: 'Status', render: (r) => <Badge text={r.status || 'ACTIVE'} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Society Blocks"
        subtitle="Registered residential building blocks and tower structures"
        action={
          <button className="btn btn-primary" onClick={() => setIsModalOpen(true)}>
            + Add Society Block
          </button>
        }
      />
      {loading ? (
        <LoadingSpinner />
      ) : (
        <Table columns={columns} data={blocks} emptyMessage="No building blocks registered yet." />
      )}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Register Building Block / Tower">
        <form onSubmit={handleCreateBlock}>
          {communityOptions.length > 0 && (
            <FormInput
              label="Select Community"
              type="select"
              options={communityOptions}
              name="communityId"
              value={formData.communityId}
              onChange={e => setFormData({ ...formData, communityId: e.target.value })}
              required
            />
          )}
          <FormInput
            label="Block Name / Tower Title"
            name="blockName"
            value={formData.blockName}
            onChange={e => setFormData({ ...formData, blockName: e.target.value })}
            placeholder="e.g. Block A or Emerald Tower"
            required
          />
          <FormInput
            label="Block Code"
            name="blockCode"
            value={formData.blockCode}
            onChange={e => setFormData({ ...formData, blockCode: e.target.value })}
            placeholder="e.g. BLK-A or TWR-EM"
          />
          <FormInput
            label="Total Floors Count"
            type="number"
            name="totalFloors"
            value={formData.totalFloors}
            onChange={e => setFormData({ ...formData, totalFloors: e.target.value })}
            placeholder="e.g. 12"
            required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }}>
            Add Building Block
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
