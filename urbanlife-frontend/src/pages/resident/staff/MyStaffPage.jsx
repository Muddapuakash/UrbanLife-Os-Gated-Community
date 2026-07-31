import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { domesticStaffApi } from '../../../api/domesticStaffApi';
import { staffRatingApi } from '../../../api/staffRatingApi';
import { staffAttendanceApi } from '../../../api/staffAttendanceApi';
import { userApi } from '../../../api/userApi';
import { residentApi } from '../../../api/residentApi';
import { useAuth } from '../../../auth/AuthContext';

export default function MyStaffPage() {
  const { user } = useAuth();
  const communityId = Number(user?.communityId || localStorage.getItem('communityId') || 1);

  const [activeTab, setActiveTab] = useState('MY_HELPERS'); // 'MY_HELPERS' | 'DIRECTORY'
  const [allStaff, setAllStaff] = useState([]);
  const [myResidentId, setMyResidentId] = useState(null);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  // Modals
  const [showRegisterModal, setShowRegisterModal] = useState(false);
  const [showRatingModal, setShowRatingModal] = useState(false);
  const [showHistoryModal, setShowHistoryModal] = useState(false);
  const [selectedStaff, setSelectedStaff] = useState(null);
  const [attendanceHistory, setAttendanceHistory] = useState([]);

  // Form states
  const [regForm, setRegForm] = useState({
    name: '', phone: '', staffType: 'MAID', customStaffType: '', address: '', verificationReference: ''
  });
  const [ratingVal, setRatingVal] = useState(5);
  const [reviewText, setReviewText] = useState('');
  const [modalError, setModalError] = useState('');
  const [modalSuccess, setModalSuccess] = useState('');

  // Assignments tracking
  const [assignedStaffIds, setAssignedStaffIds] = useState(new Set());
  const [assignmentsMap, setAssignmentsMap] = useState({}); // staffId -> assignmentId

  const loadData = async () => {
    setLoading(true);
    try {
      // 1. Resolve Resident ID
      let uid = Number(user?.userId || user?.id || localStorage.getItem('userId'));
      if (!uid) {
        const email = user?.email || localStorage.getItem('email');
        if (email) {
          const uRes = await userApi.getByEmail(email).catch(() => null);
          uid = uRes?.data?.userId || uRes?.data?.id;
        }
      }
      let rid = null;
      if (uid) {
        const rRes = await residentApi.getByUserId(uid).catch(() => null);
        rid = rRes?.data?.residentId || rRes?.data?.id;
        setMyResidentId(rid);
      }

      // 2. Load Community Staff
      const staffRes = await domesticStaffApi.getByCommunity(communityId);
      const staffList = staffRes.data || [];
      setAllStaff(staffList);

      // 3. Load Assignments for each staff
      const assignedIds = new Set();
      const aMap = {};
      await Promise.allSettled(
        staffList.map(async (s) => {
          const sid = s.staffId || s.id;
          const aRes = await domesticStaffApi.getAssignments(sid).catch(() => ({ data: [] }));
          const myAssign = (aRes.data || []).find(
            a => Number(a.residentId) === Number(rid) && a.active !== false
          );
          if (myAssign) {
            assignedIds.add(sid);
            aMap[sid] = myAssign.assignmentId || myAssign.id;
          }
        })
      );
      setAssignedStaffIds(assignedIds);
      setAssignmentsMap(aMap);
    } catch (e) {
      console.error(e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, [communityId]);

  // ─── Register Domestic Helper ─────────────────────────────────────────────
  const handleRegister = async (e) => {
    e.preventDefault();
    setSaving(true);
    setModalError('');
    setModalSuccess('');

    // ── Client-side Aadhaar validation ──────────────────────────────────────
    const rawAadhaar = (regForm.verificationReference || '').trim();
    const aadhaarDigits = rawAadhaar.replace(/-/g, '');
    if (!rawAadhaar) {
      setModalError('Aadhaar number is required. All domestic helpers must have a valid Aadhaar.');
      setSaving(false);
      return;
    }
    if (!/^\d{12}$/.test(aadhaarDigits)) {
      setModalError('Enter a valid 12-digit Aadhaar number (e.g. 1234-5678-9012).');
      setSaving(false);
      return;
    }
    // Normalize to format: XXXX-XXXX-XXXX
    const normalizedAadhaar = `${aadhaarDigits.slice(0,4)}-${aadhaarDigits.slice(4,8)}-${aadhaarDigits.slice(8,12)}`;

    try {
      // Re-resolve communityId at submit time for safety
      let cid = Number(user?.communityId || localStorage.getItem('communityId') || 0);
      if (!cid) {
        const uid = Number(user?.userId || user?.id || localStorage.getItem('userId'));
        if (uid) {
          try {
            const rRes = await residentApi.getByUserId(uid);
            cid = rRes?.data?.communityId || 0;
            if (cid) localStorage.setItem('communityId', String(cid));
          } catch (e) { /* ignore */ }
        }
      }

      if (!cid) {
        setModalError('⚠️ Community not found. Please log out and log in again to refresh your session.');
        setSaving(false);
        return;
      }

      const payload = {
        ...regForm,
        verificationReference: normalizedAadhaar,
        communityId: cid
      };
      console.log('[Register Staff] Sending payload:', payload);

      const res = await domesticStaffApi.create(payload);
      const newStaffId = res.data?.staffId || res.data?.id;

      // Auto-assign to resident if resident ID resolved
      if (newStaffId && myResidentId) {
        await domesticStaffApi.assign(newStaffId, { residentId: myResidentId }).catch((assignErr) => {
          console.warn('[Register Staff] Auto-assign failed:', assignErr?.response?.data);
        });
      }

      setModalSuccess('✅ Domestic helper registered successfully! Admin will verify their Aadhaar to allow gate access.');
      setTimeout(() => {
        setShowRegisterModal(false);
        setModalSuccess('');
        setRegForm({ name: '', phone: '', staffType: 'MAID', customStaffType: '', address: '', verificationReference: '' });
        loadData();
      }, 1500);
    } catch (err) {
      const errData = err.response?.data;
      const msg = errData?.message
        || (errData?.validationErrors && Object.values(errData.validationErrors).join(', '))
        || errData?.error
        || 'Failed to register helper. Please check all fields.';
      setModalError(msg);
      console.error('[Register Staff] Error:', errData);
    } finally {
      setSaving(false);
    }
  };

  // ─── Assign / Unassign Staff to Resident's Flat ──────────────────────────
  const handleAssignToFlat = async (staffId) => {
    if (!myResidentId) {
      alert('Resident profile not linked to your account.');
      return;
    }
    try {
      await domesticStaffApi.assign(staffId, { residentId: myResidentId });
      alert('✅ Staff successfully assigned to your flat!');
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to assign staff.');
    }
  };

  const handleRemoveAssignment = async (staffId) => {
    const assignmentId = assignmentsMap[staffId];
    if (!assignmentId) {
      alert('No active assignment found for this helper.');
      return;
    }
    if (!window.confirm('Remove this helper from your flat? They will no longer appear in your assigned helpers list.')) return;
    try {
      await domesticStaffApi.removeAssignment(assignmentId);
      alert('✅ Helper removed from your flat successfully.');
      loadData();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to remove assignment.';
      alert('❌ ' + msg);
    }
  };

  // ─── Delete Staff Permanently from System ─────────────────────────────────
  const handleDeleteStaff = async (staffId, staffName) => {
    if (!window.confirm(
      `⚠️ Permanently delete "${staffName}" from the community directory?\n\nThis cannot be undone. All their assignments and records will be removed.`
    )) return;
    try {
      await domesticStaffApi.deleteStaff(staffId);
      alert(`✅ ${staffName} has been deleted from the system.`);
      loadData();
    } catch (err) {
      const msg = err.response?.data?.message || 'Failed to delete staff.';
      alert('❌ ' + msg);
    }
  };

  // ─── Submit Rating ────────────────────────────────────────────────────────
  const handleRatingSubmit = async (e) => {
    e.preventDefault();
    if (!selectedStaff) return;
    setSaving(true);
    setModalError('');
    setModalSuccess('');

    try {
      if (!myResidentId) {
        setModalError('Resident profile not found for your login account.');
        return;
      }

      const staffId = selectedStaff.staffId || selectedStaff.id;
      await staffRatingApi.addRating(staffId, {
        residentId: Number(myResidentId),
        rating: Number(ratingVal),
        review: reviewText
      });

      setModalSuccess('⭐ Rating submitted successfully!');
      setTimeout(() => {
        setShowRatingModal(false);
        setModalSuccess('');
        setReviewText('');
        loadData();
      }, 1200);
    } catch (err) {
      setModalError(err.response?.data?.message || err.response?.data?.error || 'Failed to submit rating.');
    } finally {
      setSaving(false);
    }
  };

  // ─── View Attendance History ──────────────────────────────────────────────
  const handleViewHistory = async (staff) => {
    setSelectedStaff(staff);
    setShowHistoryModal(true);
    try {
      const res = await staffAttendanceApi.getStaffHistory(staff.staffId || staff.id);
      setAttendanceHistory(res.data || []);
    } catch (e) {
      setAttendanceHistory([]);
    }
  };

  // Filter staff data for active tab
  const myStaffList = allStaff.filter(s => assignedStaffIds.has(s.staffId || s.id));
  const displayedStaff = activeTab === 'MY_HELPERS' ? myStaffList : allStaff;

  const columns = [
    { header: 'ID', render: (r) => `#${r.staffId || r.id}` },
    { header: 'Staff Name', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.name}</strong> },
    { header: 'Phone', accessorKey: 'phone' },
    { header: 'Role', render: (r) => <span style={{ textTransform: 'capitalize' }}>{(r.staffType || '').toLowerCase().replace('_', ' ')}</span> },
    { header: 'Rating', render: (r) => r.averageRating > 0 ? `${'⭐'.repeat(Math.round(r.averageRating))} (${r.averageRating.toFixed(1)})` : '—' },
    { header: 'Aadhaar ID', render: (r) => r.verificationReference ? `XXXX-XXXX-${r.verificationReference.slice(-4)}` : 'N/A' },
    {
      header: 'Verification',
      render: (r) => (
        <Badge
          text={r.verificationStatus || 'PENDING'}
          variant={r.verificationStatus === 'VERIFIED' ? 'success' : 'warning'}
        />
      )
    },
    {
      header: 'Actions',
      render: (r) => {
        const sid = r.staffId || r.id;
        const isAssigned = assignedStaffIds.has(sid);

        return (
          <div style={{ display: 'flex', gap: '6px' }}>
            {!isAssigned ? (
              <>
                <button
                  className="btn btn-primary"
                  style={{ fontSize: '0.75rem', padding: '4px 10px' }}
                  onClick={() => handleAssignToFlat(sid)}
                >
                  + Assign to My Flat
                </button>
                {/* Delete button — only in directory view, only for unassigned staff */}
                {activeTab === 'DIRECTORY' && (
                  <button
                    className="btn btn-secondary"
                    style={{ fontSize: '0.75rem', padding: '4px 10px', color: 'var(--danger)', borderColor: 'var(--danger)' }}
                    onClick={() => handleDeleteStaff(sid, r.name)}
                    title="Permanently delete this staff from the community"
                  >
                    🗑️ Delete
                  </button>
                )}
              </>
            ) : (
              <button
                className="btn btn-secondary"
                style={{ fontSize: '0.75rem', padding: '4px 10px', color: 'var(--danger)', borderColor: 'var(--danger)' }}
                onClick={() => handleRemoveAssignment(sid)}
              >
                Remove
              </button>
            )}
            <button
              className="btn btn-secondary"
              style={{ fontSize: '0.75rem', padding: '4px 10px' }}
              onClick={() => { setSelectedStaff(r); setShowRatingModal(true); setModalError(''); setModalSuccess(''); }}
            >
              ⭐ Rate Helper
            </button>
            <button
              className="btn btn-secondary"
              style={{ fontSize: '0.75rem', padding: '4px 10px' }}
              onClick={() => handleViewHistory(r)}
            >
              🕒 Gate Log
            </button>
          </div>
        );
      }
    }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Domestic Helpers & Staff"
        subtitle="Manage personal maids, cooks, and drivers working in your flat"
        action={
          <button className="btn btn-primary" onClick={() => { setShowRegisterModal(true); setModalError(''); setModalSuccess(''); }}>
            + Register New Domestic Helper
          </button>
        }
      />

      {/* Workflow Explanation banner */}
      <div style={{
        background: 'rgba(99,102,241,0.1)',
        border: '1px solid rgba(99,102,241,0.3)',
        borderRadius: '10px',
        padding: '12px 16px',
        marginBottom: '20px',
        fontSize: '0.85rem',
        color: 'var(--text-secondary)',
        lineHeight: '1.6'
      }}>
        <strong style={{ color: 'var(--primary)' }}>🔄 Domestic Helper Workflow:</strong>
        {' '}1. <strong>Register/Assign Helper</strong> to your flat → 2. <strong>Admin Verifies ID</strong> (Aadhaar proof) & generates Gate Passcode → 3. <strong>Security scans passcode</strong> at gate for daily entry check-in → 4. <strong>Rate service</strong> after work!
      </div>

      {/* Tabs */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '20px', borderBottom: '1px solid var(--border)', paddingBottom: '10px' }}>
        <button
          onClick={() => setActiveTab('MY_HELPERS')}
          style={{
            background: activeTab === 'MY_HELPERS' ? 'var(--primary)' : 'transparent',
            color: activeTab === 'MY_HELPERS' ? '#000' : 'var(--text-secondary)',
            border: 'none',
            borderRadius: '8px',
            padding: '8px 16px',
            fontWeight: 700,
            cursor: 'pointer',
            fontSize: '0.85rem'
          }}
        >
          🏡 My Assigned Helpers ({myStaffList.length})
        </button>
        <button
          onClick={() => setActiveTab('DIRECTORY')}
          style={{
            background: activeTab === 'DIRECTORY' ? 'var(--primary)' : 'transparent',
            color: activeTab === 'DIRECTORY' ? '#000' : 'var(--text-secondary)',
            border: 'none',
            borderRadius: '8px',
            padding: '8px 16px',
            fontWeight: 700,
            cursor: 'pointer',
            fontSize: '0.85rem'
          }}
        >
          📖 All Society Staff Directory ({allStaff.length})
        </button>
      </div>

      {loading ? <LoadingSpinner /> : (
        <Table
          columns={columns}
          data={displayedStaff}
          emptyMessage={activeTab === 'MY_HELPERS' ? 'No domestic helpers assigned to your flat yet. Click "+ Register New Domestic Helper" or browse the Directory.' : 'No staff registered in community yet.'}
        />
      )}

      {/* ─── Register Modal ────────────────────────────────────────────────── */}
      <Modal isOpen={showRegisterModal} onClose={() => setShowRegisterModal(false)} title="Register Domestic Helper (Maid / Cook / Driver)">
        {modalError && <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px', borderRadius: '8px', marginBottom: '12px', fontSize: '0.85rem' }}>{modalError}</div>}
        {modalSuccess && <div style={{ background: 'rgba(16,185,129,0.15)', color: 'var(--success)', padding: '10px', borderRadius: '8px', marginBottom: '12px', fontSize: '0.85rem' }}>{modalSuccess}</div>}

        <form onSubmit={handleRegister}>
          <FormInput label="Full Name *" placeholder="e.g. Sunita Devi" value={regForm.name} onChange={e => setRegForm({ ...regForm, name: e.target.value })} required />
          <FormInput label="Phone Number *" placeholder="e.g. 9876543210" value={regForm.phone} onChange={e => setRegForm({ ...regForm, phone: e.target.value })} required />
          <FormInput label="Staff Role *" type="select"
            options={[
              { label: '🧹 Maid / Housekeeping', value: 'MAID' },
              { label: '🍳 Cook / Chef', value: 'COOK' },
              { label: '🚗 Driver', value: 'DRIVER' },
              { label: '🧼 Cleaner', value: 'CLEANER' },
              { label: '👶 Babysitter / Nanny', value: 'BABYSITTER' },
              { label: '🚗 Car Washer', value: 'CAR_WASHER' },
              { label: '🪴 Gardener', value: 'GARDENER' },
              { label: '📦 Other', value: 'OTHER' }
            ]}
            value={regForm.staffType} onChange={e => setRegForm({ ...regForm, staffType: e.target.value })} required
          />

          {/* Aadhaar field with hint */}
          <FormInput
            label="Aadhaar Number * (12-digit, unique)"
            placeholder="e.g. 1234-5678-9012"
            value={regForm.verificationReference}
            onChange={e => {
              // Auto-format as XXXX-XXXX-XXXX
              const digits = e.target.value.replace(/\D/g, '').slice(0, 12);
              const formatted = digits.length > 8
                ? `${digits.slice(0,4)}-${digits.slice(4,8)}-${digits.slice(8)}`
                : digits.length > 4
                ? `${digits.slice(0,4)}-${digits.slice(4)}`
                : digits;
              setRegForm({ ...regForm, verificationReference: formatted });
            }}
            required
          />
          <div style={{ fontSize: '0.75rem', color: 'var(--text-muted)', marginTop: '-8px', marginBottom: '12px' }}>
            🔒 Aadhaar is stored securely and must be unique. Admin will verify before allowing gate access.
          </div>

          <FormInput label="Local Address" placeholder="e.g. 12 Gandhi Nagar, Hyderabad" value={regForm.address} onChange={e => setRegForm({ ...regForm, address: e.target.value })} />

          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? '⏳ Registering...' : '✅ Register Helper'}
          </button>
        </form>
      </Modal>

      {/* ─── Rate Modal ────────────────────────────────────────────────────── */}
      <Modal isOpen={showRatingModal} onClose={() => setShowRatingModal(false)} title={`Rate Service: ${selectedStaff?.name || ''}`}>
        {modalError && <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px', borderRadius: '8px', marginBottom: '12px', fontSize: '0.85rem' }}>{modalError}</div>}
        {modalSuccess && <div style={{ background: 'rgba(16,185,129,0.15)', color: 'var(--success)', padding: '10px', borderRadius: '8px', marginBottom: '12px', fontSize: '0.85rem' }}>{modalSuccess}</div>}

        <form onSubmit={handleRatingSubmit}>
          <FormInput label="Rating (1 to 5 Stars) *" type="select"
            options={[
              { label: '⭐⭐⭐⭐⭐ (5 - Excellent)', value: '5' },
              { label: '⭐⭐⭐⭐ (4 - Very Good)', value: '4' },
              { label: '⭐⭐⭐ (3 - Average)', value: '3' },
              { label: '⭐⭐ (2 - Below Average)', value: '2' },
              { label: '⭐ (1 - Poor)', value: '1' }
            ]}
            value={ratingVal} onChange={e => setRatingVal(e.target.value)} required
          />
          <FormInput label="Review / Feedback" placeholder="Punctual, trustworthy, polite..."
            value={reviewText} onChange={e => setReviewText(e.target.value)}
          />
          <button type="submit" className="btn btn-primary" style={{ width: '100%', marginTop: '12px' }} disabled={saving}>
            {saving ? 'Submitting...' : 'Submit Rating'}
          </button>
        </form>
      </Modal>

      {/* ─── Attendance History Modal ──────────────────────────────────────── */}
      <Modal isOpen={showHistoryModal} onClose={() => setShowHistoryModal(false)} title={`Gate Check-In History: ${selectedStaff?.name || ''}`}>
        <div style={{ maxHeight: '400px', overflowY: 'auto' }}>
          {attendanceHistory.length === 0 ? (
            <p style={{ color: 'var(--text-muted)', textAlign: 'center', padding: '20px' }}>No gate check-in logs recorded for this staff member.</p>
          ) : (
            <table style={{ width: '100%', borderCollapse: 'collapse', fontSize: '0.85rem' }}>
              <thead>
                <tr style={{ borderBottom: '1px solid var(--border)', textAlign: 'left' }}>
                  <th style={{ padding: '8px' }}>Entry Time</th>
                  <th style={{ padding: '8px' }}>Exit Time</th>
                  <th style={{ padding: '8px' }}>Status</th>
                </tr>
              </thead>
              <tbody>
                {attendanceHistory.map((h, i) => (
                  <tr key={i} style={{ borderBottom: '1px solid var(--border)' }}>
                    <td style={{ padding: '8px', color: 'var(--success)' }}>{h.entryTime ? new Date(h.entryTime).toLocaleString() : '—'}</td>
                    <td style={{ padding: '8px', color: 'var(--text-muted)' }}>{h.exitTime ? new Date(h.exitTime).toLocaleString() : 'Still inside'}</td>
                    <td style={{ padding: '8px' }}>
                      <Badge text={h.exitTime ? 'OUT' : 'INSIDE'} variant={h.exitTime ? 'secondary' : 'success'} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </Modal>
    </DashboardLayout>
  );
}
