import { useEffect, useState } from 'react';
import DashboardLayout from '../../components/layout/DashboardLayout';
import PageHeader from '../../components/common/PageHeader';
import Table from '../../components/common/Table';
import Badge from '../../components/common/Badge';
import LoadingSpinner from '../../components/common/LoadingSpinner';
import Modal from '../../components/common/Modal';
import FormInput from '../../components/common/FormInput';
import { lostFoundApi } from '../../api/lostFoundApi';
import { itemClaimApi } from '../../api/itemClaimApi';
import { userApi } from '../../api/userApi';
import { useAuth } from '../../auth/AuthContext';

// Role check helpers
const canManage = (role) => ['SECURITY', 'ADMIN', 'SUPER_ADMIN'].includes(role?.toUpperCase());

export default function LostFoundPage() {
  const { user } = useAuth();
  const role = user?.role?.toUpperCase() || '';

  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // Item report modal state
  const [showReportModal, setShowReportModal] = useState(false);
  const [reportError, setReportError] = useState('');
  const [reportSuccess, setReportSuccess] = useState('');
  const [reportForm, setReportForm] = useState({
    itemName: '', description: '', category: 'KEYS', reportType: 'FOUND', location: ''
  });

  // Claim modal state
  const [showClaimModal, setShowClaimModal] = useState(false);
  const [selectedItem, setSelectedItem] = useState(null);
  const [proofText, setProofText] = useState('');
  const [claimError, setClaimError] = useState('');
  const [claimSuccess, setClaimSuccess] = useState('');

  // Pending claims panel (Security/Admin only)
  const [pendingClaims, setPendingClaims] = useState([]);
  const [claimsLoading, setClaimsLoading] = useState(false);
  const [actioningClaimId, setActioningClaimId] = useState(null);

  // ─── Helpers ──────────────────────────────────────────────────────────────
  const getResolvedUserId = async () => {
    let uid = Number(user?.userId || user?.id || localStorage.getItem('userId'));
    if (!uid) {
      const email = user?.email || localStorage.getItem('email');
      if (email) {
        try {
          const res = await userApi.getByEmail(email);
          uid = res.data?.userId || res.data?.id;
          if (uid) localStorage.setItem('userId', String(uid));
        } catch (e) { console.error(e); }
      }
    }
    return uid || null;
  };

  // ─── Load items ───────────────────────────────────────────────────────────
  const loadItems = () => {
    setLoading(true);
    lostFoundApi.getByCommunity(1)
      .then(res => setItems(res.data || []))
      .catch(err => console.error('[LostFound] load items error', err))
      .finally(() => setLoading(false));
  };

  // Load pending claims for each found+open item (Security/Admin)
  const loadPendingClaims = async (itemsList) => {
    if (!canManage(role)) return;
    setClaimsLoading(true);
    const foundOpenItems = itemsList.filter(i => i.reportType === 'FOUND' && i.status === 'OPEN');
    const allClaims = [];
    for (const item of foundOpenItems) {
      try {
        const res = await itemClaimApi.getByItem(item.itemId || item.id);
        const pending = (res.data || [])
          .filter(c => c.status === 'PENDING')
          .map(c => ({ ...c, itemName: item.itemName }));
        allClaims.push(...pending);
      } catch (e) { /* no claims yet */ }
    }
    setPendingClaims(allClaims);
    setClaimsLoading(false);
  };

  useEffect(() => {
    loadItems();
  }, []);

  useEffect(() => {
    if (items.length > 0) loadPendingClaims(items);
  }, [items]);

  // ─── Report Item ──────────────────────────────────────────────────────────
  const handleReport = async (e) => {
    e.preventDefault();
    setSaving(true);
    setReportError('');
    setReportSuccess('');

    try {
      const uid = await getResolvedUserId();
      if (!uid) {
        setReportError('Unable to identify your user account. Please log out and log back in.');
        return;
      }

      const payload = {
        itemName: reportForm.itemName,
        description: reportForm.description || 'No description provided.',
        category: reportForm.category,
        reportType: reportForm.reportType,
        location: reportForm.location,
        incidentTime: new Date().toISOString().slice(0, 19),
        userId: uid,
        communityId: 1
      };

      await lostFoundApi.reportItem(payload);
      setReportSuccess('✅ Item reported successfully!');
      setTimeout(() => {
        setShowReportModal(false);
        setReportSuccess('');
        setReportForm({ itemName: '', description: '', category: 'KEYS', reportType: 'FOUND', location: '' });
        loadItems();
      }, 1200);
    } catch (err) {
      const status = err.response?.status;
      const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'Unknown error';
      console.error('[LostFound] Report failed:', status, msg);
      setReportError(`Error ${status || ''}: ${msg}`);
    } finally {
      setSaving(false);
    }
  };

  // ─── Claim Item ───────────────────────────────────────────────────────────
  const handleClaimSubmit = async (e) => {
    e.preventDefault();
    if (!selectedItem) return;
    setSaving(true);
    setClaimError('');
    setClaimSuccess('');

    try {
      const uid = await getResolvedUserId();
      if (!uid) {
        setClaimError('Unable to identify your user account. Please log out and log back in.');
        return;
      }

      const itemId = selectedItem.itemId || selectedItem.id;
      await itemClaimApi.claimItem(itemId, uid, { proofDescription: proofText });
      setClaimSuccess('✅ Claim request submitted! Awaiting Security/Admin approval.');
      setTimeout(() => {
        setShowClaimModal(false);
        setClaimSuccess('');
        setProofText('');
        loadItems();
      }, 1500);
    } catch (err) {
      const msg = err.response?.data?.message || err.response?.data?.error || err.message || 'Unknown error';
      console.error('[LostFound] Claim failed:', err.response?.status, msg);
      setClaimError(msg);
    } finally {
      setSaving(false);
    }
  };

  // ─── Approve / Reject Claim ────────────────────────────────────────────────
  const handleApproveClaim = async (claimId) => {
    setActioningClaimId(claimId);
    try {
      await itemClaimApi.approve(claimId);
      loadItems(); // triggers pending claims reload via useEffect
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to approve claim.');
    } finally {
      setActioningClaimId(null);
    }
  };

  const handleRejectClaim = async (claimId) => {
    setActioningClaimId(claimId);
    try {
      await itemClaimApi.reject(claimId);
      loadItems();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to reject claim.');
    } finally {
      setActioningClaimId(null);
    }
  };

  // ─── Table columns ────────────────────────────────────────────────────────
  const statusVariant = { OPEN: 'warning', CLAIMED: 'success', RETURNED: 'info', CLOSED: 'secondary' };

  const itemColumns = [
    { header: 'ID', render: (r) => <span style={{ color: 'var(--text-muted)' }}>#{r.itemId || r.id}</span> },
    { header: 'Item Name', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.itemName}</strong> },
    {
      header: 'Report Type',
      render: (r) => <Badge text={r.reportType} variant={r.reportType === 'LOST' ? 'danger' : 'info'} />
    },
    { header: 'Category', render: (r) => <span style={{ textTransform: 'capitalize', fontSize: '0.85rem' }}>{(r.category || '').toLowerCase().replace('_', ' ')}</span> },
    { header: 'Location', render: (r) => <span style={{ fontSize: '0.85rem' }}>{r.location}</span> },
    {
      header: 'Status',
      render: (r) => <Badge text={r.status || 'OPEN'} variant={statusVariant[r.status] || 'warning'} />
    },
    {
      header: 'Action',
      render: (r) => {
        if (r.status !== 'OPEN') return <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>—</span>;
        if (r.reportType !== 'FOUND') return <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Awaiting match</span>;
        return (
          <button
            className="btn btn-secondary"
            style={{ fontSize: '0.75rem', padding: '4px 12px' }}
            onClick={() => { setSelectedItem(r); setShowClaimModal(true); setClaimError(''); setClaimSuccess(''); }}
          >
            Claim Item
          </button>
        );
      }
    }
  ];

  const claimColumns = [
    { header: 'Claim ID', render: (r) => <span style={{ color: 'var(--text-muted)' }}>#{r.claimId}</span> },
    { header: 'Item', render: (r) => <strong>{r.itemName}</strong> },
    { header: 'Claimant (User ID)', render: (r) => `User #${r.userId}` },
    { header: 'Proof Provided', render: (r) => <span style={{ fontSize: '0.82rem', color: 'var(--text-secondary)' }}>{r.proofDescription}</span> },
    {
      header: 'Actions',
      render: (r) => (
        <div style={{ display: 'flex', gap: '8px' }}>
          <button
            className="btn btn-primary"
            style={{ fontSize: '0.75rem', padding: '4px 12px' }}
            disabled={actioningClaimId === r.claimId}
            onClick={() => handleApproveClaim(r.claimId)}
          >
            {actioningClaimId === r.claimId ? '...' : '✅ Approve'}
          </button>
          <button
            className="btn btn-secondary"
            style={{ fontSize: '0.75rem', padding: '4px 12px', color: 'var(--danger)', borderColor: 'var(--danger)' }}
            disabled={actioningClaimId === r.claimId}
            onClick={() => handleRejectClaim(r.claimId)}
          >
            {actioningClaimId === r.claimId ? '...' : '❌ Reject'}
          </button>
        </div>
      )
    }
  ];

  // ─── Render ───────────────────────────────────────────────────────────────
  return (
    <DashboardLayout>
      <PageHeader
        title="Lost & Found Desk"
        subtitle="Report found items or search for your lost belongings"
        action={
          <button className="btn btn-primary" onClick={() => { setShowReportModal(true); setReportError(''); setReportSuccess(''); }}>
            + Report Item
          </button>
        }
      />

      {/* Workflow info banner */}
      <div style={{
        background: 'rgba(99,102,241,0.1)',
        border: '1px solid rgba(99,102,241,0.3)',
        borderRadius: '10px',
        padding: '12px 16px',
        marginBottom: '24px',
        fontSize: '0.85rem',
        color: 'var(--text-secondary)',
        lineHeight: '1.6'
      }}>
        <strong style={{ color: 'var(--primary)' }}>📌 How it works:</strong>
        {' '}Security/Resident reports a <strong>Found</strong> item → Residents <strong>Claim</strong> it with proof →
        Security <strong>Approves</strong> the claim → Item status becomes <strong>CLAIMED</strong> → Security marks it <strong>RETURNED</strong> upon handover.
        <br />
        <em>Note: Claiming an item only submits a request. The item stays OPEN until Security/Admin approves it.</em>
      </div>

      {/* Items Table */}
      <div style={{ marginBottom: '32px' }}>
        <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px', color: 'var(--text-primary)' }}>
          📦 Community Items
        </h3>
        {loading ? <LoadingSpinner /> : (
          <Table columns={itemColumns} data={items} emptyMessage="No lost or found items reported yet" />
        )}
      </div>

      {/* Pending Claims Panel — Security/Admin only */}
      {canManage(role) && (
        <div>
          <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px', color: 'var(--text-primary)', display: 'flex', alignItems: 'center', gap: '8px' }}>
            🕐 Pending Claim Requests
            {pendingClaims.length > 0 && (
              <span style={{ background: 'var(--danger)', color: '#fff', borderRadius: '999px', fontSize: '0.72rem', padding: '2px 8px' }}>
                {pendingClaims.length}
              </span>
            )}
          </h3>
          {claimsLoading ? <LoadingSpinner /> : (
            <Table
              columns={claimColumns}
              data={pendingClaims}
              emptyMessage="No pending claim requests"
            />
          )}
        </div>
      )}

      {/* ─── Report Modal ──────────────────────────────────────────────────── */}
      <Modal isOpen={showReportModal} onClose={() => setShowReportModal(false)} title="Report Lost or Found Item">
        {reportError && (
          <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '12px' }}>
            {reportError}
          </div>
        )}
        {reportSuccess && (
          <div style={{ background: 'rgba(16,185,129,0.15)', color: 'var(--success)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '12px' }}>
            {reportSuccess}
          </div>
        )}
        <form onSubmit={handleReport}>
          <FormInput label="Report Type *" type="select"
            options={[{ label: '🔍 I Found an Item', value: 'FOUND' }, { label: '❌ I Lost an Item', value: 'LOST' }]}
            value={reportForm.reportType}
            onChange={e => setReportForm({ ...reportForm, reportType: e.target.value })} required
          />
          <FormInput label="Item Name *" placeholder="e.g. Car Key, Wallet, Phone"
            value={reportForm.itemName}
            onChange={e => setReportForm({ ...reportForm, itemName: e.target.value })} required
          />
          <FormInput label="Category *" type="select"
            options={[
              { label: 'Keys', value: 'KEYS' }, { label: 'Electronics', value: 'ELECTRONICS' },
              { label: 'Wallet', value: 'WALLET' }, { label: 'Documents', value: 'DOCUMENTS' },
              { label: 'Jewellery', value: 'JEWELLERY' }, { label: 'Clothing', value: 'CLOTHING' },
              { label: 'Bag', value: 'BAG' }, { label: 'Pet', value: 'PET' },
              { label: 'Vehicle Accessory', value: 'VEHICLE_ACCESSORY' }, { label: 'Other', value: 'OTHER' }
            ]}
            value={reportForm.category}
            onChange={e => setReportForm({ ...reportForm, category: e.target.value })} required
          />
          <FormInput label="Location Found/Lost *" placeholder="e.g. Near Club House Swimming Pool"
            value={reportForm.location}
            onChange={e => setReportForm({ ...reportForm, location: e.target.value })} required
          />
          <FormInput label="Description *" placeholder="Describe the item in detail (color, brand, markings)..."
            value={reportForm.description}
            onChange={e => setReportForm({ ...reportForm, description: e.target.value })} required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? 'Submitting...' : 'Submit Report'}
          </button>
        </form>
      </Modal>

      {/* ─── Claim Modal ───────────────────────────────────────────────────── */}
      <Modal isOpen={showClaimModal} onClose={() => setShowClaimModal(false)} title={`Claim: ${selectedItem?.itemName || ''}`}>
        <div style={{ background: 'rgba(99,102,241,0.1)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.83rem', marginBottom: '16px', color: 'var(--text-secondary)' }}>
          ℹ️ Your claim will be <strong>reviewed by Security</strong> before the item is handed over. Status will remain <strong>OPEN</strong> until approved.
        </div>
        {claimError && (
          <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '12px' }}>
            {claimError}
          </div>
        )}
        {claimSuccess && (
          <div style={{ background: 'rgba(16,185,129,0.15)', color: 'var(--success)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '12px' }}>
            {claimSuccess}
          </div>
        )}
        <form onSubmit={handleClaimSubmit}>
          <FormInput label="Proof of Ownership *"
            value={proofText}
            onChange={e => setProofText(e.target.value)}
            placeholder="Describe identifying marks, color, serial number, key brand..."
            required
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? 'Submitting...' : 'Submit Claim Request'}
          </button>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
