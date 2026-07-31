import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import FormInput from '../../../components/common/FormInput';
import { domesticStaffApi } from '../../../api/domesticStaffApi';
import { staffAttendanceApi } from '../../../api/staffAttendanceApi';
import { userApi } from '../../../api/userApi';
import { useAuth } from '../../../auth/AuthContext';

export default function StaffEntryPage() {
  const { user } = useAuth();
  const communityId = Number(user?.communityId || localStorage.getItem('communityId') || 1);

  const [insideStaff, setInsideStaff] = useState([]);
  const [allStaff, setAllStaff] = useState([]);
  const [selectedStaffId, setSelectedStaffId] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);

  const loadData = () => {
    setLoading(true);
    Promise.all([
      staffAttendanceApi.getCurrentlyInside(communityId).catch(() => ({ data: [] })),
      domesticStaffApi.getByCommunity(communityId).catch(() => ({ data: [] }))
    ]).then(([insideRes, staffRes]) => {
      const inside = insideRes.data || [];
      const staff = staffRes.data || [];
      setInsideStaff(inside);
      setAllStaff(staff);

      // Pre-select first staff not inside
      const insideStaffIds = new Set(inside.map(i => i.staffId));
      const outside = staff.filter(s => !insideStaffIds.has(s.staffId));
      if (outside.length > 0) {
        setSelectedStaffId(outside[0].staffId);
      }
    }).finally(() => setLoading(false));
  };

  useEffect(() => { loadData(); }, [communityId]);

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
    return uid || 1;
  };

  const handleEntry = async (e) => {
    e.preventDefault();
    if (!selectedStaffId) return;
    setSaving(true);

    try {
      const guardUserId = await getResolvedUserId();
      await staffAttendanceApi.recordEntry(Number(selectedStaffId), guardUserId);
      alert('✅ Entry recorded successfully!');
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to record entry');
    } finally {
      setSaving(false);
    }
  };

  const handleExit = async (staffId) => {
    try {
      const guardUserId = await getResolvedUserId();
      await staffAttendanceApi.recordExit(staffId, guardUserId);
      alert('✅ Exit recorded successfully!');
      loadData();
    } catch (err) {
      alert(err.response?.data?.message || 'Failed to record exit');
    }
  };

  const columns = [
    { header: 'ID', render: (r) => `#${r.attendanceId}` },
    { header: 'Staff Name', render: (r) => <strong style={{ color: 'var(--primary)' }}>{r.staffName || `Staff #${r.staffId}`}</strong> },
    { header: 'Entry Time', render: (r) => r.entryTime ? new Date(r.entryTime).toLocaleTimeString() : '—' },
    { header: 'Recorded By', render: (r) => r.entryRecordedByName || `Guard #${r.entryRecordedById}` },
    {
      header: 'Status',
      render: (r) => <Badge text={r.status || 'INSIDE'} variant="success" />
    },
    {
      header: 'Action',
      render: (r) => (
        <button
          className="btn btn-secondary"
          style={{ padding: '4px 10px', fontSize: '0.75rem', color: 'var(--danger)', borderColor: 'var(--danger)' }}
          onClick={() => handleExit(r.staffId)}
        >
          Record Exit 🚪
        </button>
      )
    }
  ];

  const insideStaffIds = new Set(insideStaff.map(i => i.staffId));
  const outsideStaff = allStaff.filter(s => !insideStaffIds.has(s.staffId));

  return (
    <DashboardLayout>
      <PageHeader
        title="Domestic Staff Gate Check-In"
        subtitle="Record daily entry & exit for maids, cooks, drivers, plumbers & gardeners"
      />

      <div className="card" style={{ marginBottom: '28px' }}>
        <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px' }}>🚪 Record Staff Gate Entry</h3>
        <form onSubmit={handleEntry} style={{ display: 'flex', gap: '12px', alignItems: 'flex-end' }}>
          <div style={{ flex: 1 }}>
            <FormInput
              label="Select Domestic Staff *"
              type="select"
              options={outsideStaff.map(s => ({
                label: `${s.name} — ${(s.staffType || '').toLowerCase()} (${s.phone})`,
                value: s.staffId
              }))}
              value={selectedStaffId}
              onChange={e => setSelectedStaffId(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary" style={{ height: '42px', minWidth: '140px' }} disabled={saving || outsideStaff.length === 0}>
            {saving ? 'Recording...' : 'Record Entry'}
          </button>
        </form>
      </div>

      <h3 style={{ fontSize: '1rem', fontWeight: 600, marginBottom: '12px' }}>
        📍 Staff Currently Inside Society ({insideStaff.length})
      </h3>
      {loading ? <LoadingSpinner /> : <Table columns={columns} data={insideStaff} emptyMessage="No staff currently inside the community" />}
    </DashboardLayout>
  );
}
