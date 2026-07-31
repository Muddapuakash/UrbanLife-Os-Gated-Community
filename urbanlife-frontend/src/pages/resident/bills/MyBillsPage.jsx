import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { maintenanceBillApi } from '../../../api/maintenanceBillApi';
import { maintenancePaymentApi } from '../../../api/maintenancePaymentApi';
import { residentApi } from '../../../api/residentApi';
import { formatDateOnly } from '../../../utils/formatDate';

const REFRESH_INTERVAL = 10000;

export default function MyBillsPage() {
  const [bills, setBills] = useState([]);
  const [loading, setLoading] = useState(true);
  const [resident, setResident] = useState(null);
  const [notMapped, setNotMapped] = useState(false);
  const [selectedBill, setSelectedBill] = useState(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [paymentMethod, setPaymentMethod] = useState('UPI');
  const [lastUpdated, setLastUpdated] = useState(null);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const loadBills = useCallback((flatId) => {
    maintenanceBillApi.getByFlat(flatId)
      .then(res => {
        setBills(res.data || []);
        setLastUpdated(new Date());
      })
      .catch(err => console.error('Bills fetch error:', err))
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    let interval;

    // Step 1: Try to get resident by this user's userId
    residentApi.getByUserId(currentUserId)
      .then(res => {
        const r = res.data;
        const flatId = r?.flatId;

        if (!flatId) {
          setNotMapped(true);
          setLoading(false);
          return;
        }

        setResident(r);
        loadBills(flatId);
        interval = setInterval(() => loadBills(flatId), REFRESH_INTERVAL);
      })
      .catch(() => {
        // Step 2: Fallback — get all residents in community and match by email
        const email = localStorage.getItem('email') || '';
        residentApi.getByCommunity(1)
          .then(res => {
            const found = (res.data || []).find(
              r => (r.email || '').toLowerCase() === email.toLowerCase()
            );

            if (found && found.flatId) {
              setResident(found);
              loadBills(found.flatId);
              interval = setInterval(() => loadBills(found.flatId), REFRESH_INTERVAL);
            } else {
              setNotMapped(true);
              setLoading(false);
            }
          })
          .catch(() => {
            setNotMapped(true);
            setLoading(false);
          });
      });

    return () => { if (interval) clearInterval(interval); };
  }, [currentUserId, loadBills]);

  const openPayModal = (bill) => {
    setSelectedBill(bill);
    setIsModalOpen(true);
  };

  const handlePay = (e) => {
    e.preventDefault();
    if (!selectedBill) return;

    const billId = selectedBill.billId || selectedBill.id;
    const payload = {
      amount: selectedBill.amount,
      paymentMethod: paymentMethod,
      transactionReference: 'TXN_' + Date.now(),
      remarks: `Maintenance ${selectedBill.billingMonth}/${selectedBill.billingYear}`
    };

    maintenancePaymentApi.pay(billId, payload)
      .then(() => {
        alert('🎉 Payment Successful! Bill marked as Paid.');
        setIsModalOpen(false);
        if (resident) loadBills(resident.flatId);
      })
      .catch(err => {
        console.error(err);
        alert(err.response?.data?.message || 'Payment failed. Please try again.');
      });
  };

  const columns = [
    {
      header: 'Bill #',
      render: (r) => (
        <strong style={{ color: 'var(--primary)', fontFamily: 'monospace' }}>
          #{r.billNumber || r.billId || r.id}
        </strong>
      )
    },
    { header: 'Billing Period', render: (r) => `${r.billingMonth}/${r.billingYear}` },
    {
      header: 'Amount Due',
      render: (r) => (
        <strong style={{ color: 'var(--success)', fontSize: '1.05rem' }}>₹{r.amount}</strong>
      )
    },
    { header: 'Due Date', render: (r) => formatDateOnly(r.dueDate) },
    { header: 'Status', render: (r) => <Badge text={r.status} /> },
    {
      header: 'Action',
      render: (r) => {
        const isPaid = r.status === 'PAID' || r.status === 'CLEARED';
        return isPaid
          ? <span style={{ color: 'var(--success)', fontWeight: 600 }}>✓ Paid</span>
          : (
            <button
              className="btn btn-primary"
              style={{ padding: '6px 14px', fontSize: '0.8rem' }}
              onClick={() => openPayModal(r)}
            >
              💳 Pay Now
            </button>
          );
      }
    }
  ];

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' }}>
        <PageHeader
          title="Maintenance & Dues"
          subtitle="Monthly society maintenance invoices for your flat"
        />
        <div style={{
          fontSize: '0.75rem', color: 'var(--text-muted)',
          display: 'flex', alignItems: 'center', gap: '6px',
          paddingTop: '8px', whiteSpace: 'nowrap'
        }}>
          <span style={{
            width: '8px', height: '8px', borderRadius: '50%',
            background: 'var(--success)', display: 'inline-block'
          }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      {notMapped && (
        <div style={{
          marginBottom: '20px', padding: '14px 18px',
          background: 'rgba(239,68,68,0.1)', borderLeft: '4px solid var(--danger)', borderRadius: '8px'
        }}>
          <strong style={{ color: 'var(--danger)' }}>⚠️ Flat Mapping Required:</strong>
          <span style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginLeft: '8px' }}>
            Your account is not linked to a society flat yet. Please ask your Society Admin to map your user to a flat in
            Admin → Resident Directory.
          </span>
        </div>
      )}

      {loading
        ? <LoadingSpinner />
        : (
          <Table
            columns={columns}
            data={bills}
            emptyMessage="No maintenance invoices posted for your flat yet. Bills will appear here after Admin generates them."
          />
        )
      }

      <Modal isOpen={isModalOpen} onClose={() => setIsModalOpen(false)} title="Online Payment Checkout">
        {selectedBill && (
          <form onSubmit={handlePay}>
            <div style={{
              marginBottom: '16px', padding: '14px',
              background: 'rgba(16,185,129,0.08)',
              border: '1px solid var(--success)', borderRadius: '8px'
            }}>
              <div style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Amount Payable</div>
              <div style={{ fontSize: '1.8rem', fontWeight: 800, color: 'var(--success)' }}>
                ₹{selectedBill.amount}
              </div>
              <div style={{ fontSize: '0.82rem', color: 'var(--text-muted)', marginTop: '4px' }}>
                Period: {selectedBill.billingMonth}/{selectedBill.billingYear}
                &nbsp;|&nbsp;
                Due: {formatDateOnly(selectedBill.dueDate)}
              </div>
            </div>

            <FormInput
              label="Payment Method"
              type="select"
              options={[
                { label: '📱 UPI (GPay / PhonePe / Paytm)', value: 'UPI' },
                { label: '💳 Credit / Debit Card', value: 'CARD' },
                { label: '🏦 Net Banking', value: 'NET_BANKING' },
                { label: '💵 Cash at Society Office', value: 'CASH' }
              ]}
              name="paymentMethod"
              value={paymentMethod}
              onChange={e => setPaymentMethod(e.target.value)}
              required
            />

            <button
              type="submit"
              className="btn btn-primary"
              style={{ width: '100%', marginTop: '16px', padding: '12px', fontSize: '0.95rem' }}
            >
              Confirm & Pay ₹{selectedBill.amount}
            </button>
          </form>
        )}
      </Modal>
    </DashboardLayout>
  );
}
