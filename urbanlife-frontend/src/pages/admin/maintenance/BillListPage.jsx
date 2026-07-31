import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { maintenanceBillApi } from '../../../api/maintenanceBillApi';
import { flatApi } from '../../../api/flatApi';
import { formatDateOnly } from '../../../utils/formatDate';

export default function BillListPage() {
  const [bills, setBills] = useState([]);
  const [flats, setFlats] = useState([]);
  const [loading, setLoading] = useState(true);
  const [submitting, setSubmitting] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const today = new Date();
  const defaultDueDate = new Date(today.getTime() + 15 * 24 * 60 * 60 * 1000).toISOString().split('T')[0];

  const [formData, setFormData] = useState({
    flatId: '',
    billingYear: today.getFullYear(),
    billingMonth: today.getMonth() + 1,
    amount: '3500',
    dueDate: defaultDueDate,
    description: 'Monthly Society Maintenance Fee'
  });

  const loadData = () => {
    setLoading(true);
    Promise.allSettled([
      maintenanceBillApi.getByCommunity(1),
      flatApi.getByCommunity(1)
    ]).then(([billsRes, flatsRes]) => {
      if (billsRes.status === 'fulfilled') setBills(billsRes.value.data || []);
      if (flatsRes.status === 'fulfilled') {
        const list = flatsRes.value.data || [];
        setFlats(list);
        if (list.length > 0) {
          setFormData(prev => ({
            ...prev,
            flatId: String(list[0].flatId || list[0].id)
          }));
        }
      }
    }).finally(() => setLoading(false));
  };

  useEffect(() => {
    loadData();
  }, []);

  const openModal = () => {
    if (flats.length > 0 && !formData.flatId) {
      setFormData(prev => ({ ...prev, flatId: String(flats[0].flatId || flats[0].id) }));
    }
    setIsModalOpen(true);
  };

  const handleGenerateBill = (e) => {
    e.preventDefault();

    if (!formData.flatId) {
      alert('Please select a target flat.');
      return;
    }

    setSubmitting(true);

    const payload = {
      flatId: Number(formData.flatId),
      billingYear: Number(formData.billingYear),
      billingMonth: Number(formData.billingMonth),
      amount: Number(formData.amount),
      dueDate: formData.dueDate,
      description: formData.description || 'Monthly Maintenance Dues'
    };

    maintenanceBillApi.create(payload)
      .then(() => {
        alert('✅ Maintenance bill generated successfully!');
        setIsModalOpen(false);
        loadData();
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Failed to generate bill. Please check inputs.');
      })
      .finally(() => setSubmitting(false));
  };

  const flatOptions = flats.map(f => ({
    label: `Flat ${f.flatNumber || f.id} (${f.blockName || f.block?.blockName || 'Block A'})`,
    value: String(f.flatId || f.id)
  }));

  const columns = [
    { header: 'Bill #', render: (r) => <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>#{r.billNumber || r.billId || r.id}</strong> },
    { header: 'Flat', render: (r) => r.flatNumber || 'N/A' },
    { header: 'Amount', render: (r) => <strong style={{ color: 'var(--success)' }}>₹{r.amount}</strong> },
    { header: 'Billing Period', render: (r) => `${r.billingMonth}/${r.billingYear}` },
    { header: 'Due Date', render: (r) => formatDateOnly(r.dueDate) },
    { header: 'Status', render: (r) => <Badge text={r.status} /> }
  ];

  return (
    <DashboardLayout>
      <PageHeader 
        title="Maintenance Billing & Finance" 
        subtitle="Generate and manage monthly society maintenance invoices for flats" 
        action={<button className="btn btn-primary" onClick={openModal}>+ Issue New Invoice</button>}
      />
      {loading ? <LoadingSpinner /> : <Table columns={columns} data={bills} emptyMessage="No maintenance invoices generated yet" />}

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Issue Society Maintenance Bill">
        <form onSubmit={handleGenerateBill}>
          <FormInput 
            label="Select Target Flat" 
            type="select" 
            options={flatOptions} 
            name="flatId" 
            value={formData.flatId} 
            onChange={e => setFormData({ ...formData, flatId: e.target.value })} 
            required 
          />
          <FormInput 
            label="Bill Amount (₹)" 
            type="number" 
            name="amount" 
            value={formData.amount} 
            onChange={e => setFormData({ ...formData, amount: e.target.value })} 
            required 
          />
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <FormInput 
              label="Billing Month (1-12)" 
              type="number" 
              name="billingMonth" 
              value={formData.billingMonth} 
              onChange={e => setFormData({ ...formData, billingMonth: e.target.value })} 
              required 
            />
            <FormInput 
              label="Billing Year" 
              type="number" 
              name="billingYear" 
              value={formData.billingYear} 
              onChange={e => setFormData({ ...formData, billingYear: e.target.value })} 
              required 
            />
          </div>
          <FormInput 
            label="Due Date" 
            type="date" 
            name="dueDate" 
            value={formData.dueDate} 
            onChange={e => setFormData({ ...formData, dueDate: e.target.value })} 
            required 
          />
          <button 
            type="submit" 
            className="btn btn-primary" 
            disabled={submitting}
            style={{ width: '100%', marginTop: '14px', padding: '10px' }}
          >
            {submitting ? 'Generating...' : 'Generate & Issue Invoice'}
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
