import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import Modal from '../../../components/common/Modal';
import FormInput from '../../../components/common/FormInput';
import { userApi } from '../../../api/userApi';
import { communityApi } from '../../../api/communityApi';
import { residentApi } from '../../../api/residentApi';
import { roleApi } from '../../../api/roleApi';

const ROLE_COLORS = {
  SUPER_ADMIN: { color: '#ef4444', icon: '👑' },
  ADMIN:       { color: '#6366f1', icon: '🛡️' },
  RESIDENT:    { color: '#10b981', icon: '🏠' },
  SECURITY:    { color: '#3b82f6', icon: '🔒' },
  STAFF:       { color: '#f59e0b', icon: '🔧' },
};

export default function UserListPage() {
  const [users, setUsers] = useState([]);
  const [communities, setCommunities] = useState([]);
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showModal, setShowModal] = useState(false);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Filters
  const [filterRole, setFilterRole] = useState('ALL');
  const [filterCommunity, setFilterCommunity] = useState('ALL');
  const [searchText, setSearchText] = useState('');

  // Community map for users (built from residentApi)
  const [userCommunityMap, setUserCommunityMap] = useState({}); // userId -> { communityId, communityName }

  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    roleId: 4
  });

  const loadData = async () => {
    setLoading(true);
    try {
      const [usersRes, comRes, roleRes] = await Promise.all([
        userApi.getAll().catch(() => ({ data: [] })),
        communityApi.getAll().catch(() => ({ data: [] })),
        roleApi.getAll().catch(() => ({ data: [] }))
      ]);

      const userList = usersRes.data || [];
      const comList = comRes.data || [];
      const roleList = roleRes.data || [];

      setUsers(userList);
      setCommunities(comList);
      setRoles(roleList);

      // Build community map by loading residents for each community
      const comMap = {};
      await Promise.allSettled(
        comList.map(async (c) => {
          const cid = c.communityId || c.id;
          try {
            const res = await residentApi.getByCommunity(cid);
            (res.data || []).forEach(r => {
              if (r.userId) {
                comMap[r.userId] = {
                  communityId: cid,
                  communityName: c.communityName || c.name
                };
              }
            });
          } catch (e) { /* ignore */ }
        })
      );
      setUserCommunityMap(comMap);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadData(); }, []);

  const handleCreateUser = async (e) => {
    e.preventDefault();
    setSaving(true);
    setError('');
    setSuccess('');

    try {
      await userApi.create({ ...formData, roleId: Number(formData.roleId) });
      setSuccess('✅ User account provisioned successfully!');
      setTimeout(() => {
        setShowModal(false);
        setSuccess('');
        setFormData({ firstName: '', lastName: '', email: '', phone: '', password: '', roleId: 4 });
        loadData();
      }, 1200);
    } catch (err) {
      setError(err.response?.data?.message || JSON.stringify(err.response?.data) || 'Failed to create user.');
    } finally {
      setSaving(false);
    }
  };

  // ─── Filtered users ───────────────────────────────────────────────────────
  const filteredUsers = users.filter(u => {
    const roleName = String(u.roleName || '').replace('ROLE_', '').toUpperCase();
    if (filterRole !== 'ALL' && roleName !== filterRole) return false;

    if (filterCommunity !== 'ALL') {
      const comInfo = userCommunityMap[u.userId];
      if (!comInfo || String(comInfo.communityId) !== String(filterCommunity)) return false;
    }

    if (searchText) {
      const q = searchText.toLowerCase();
      const fullName = `${u.firstName || ''} ${u.lastName || ''}`.toLowerCase();
      const email = (u.email || '').toLowerCase();
      const phone = (u.phone || '').toLowerCase();
      if (!fullName.includes(q) && !email.includes(q) && !phone.includes(q)) return false;
    }

    return true;
  });

  // ─── Stats by role ────────────────────────────────────────────────────────
  const roleStats = ['SUPER_ADMIN', 'ADMIN', 'RESIDENT', 'SECURITY', 'STAFF'].map(r => ({
    role: r,
    count: users.filter(u => String(u.roleName || '').replace('ROLE_', '').toUpperCase() === r).length,
    ...ROLE_COLORS[r]
  }));

  const columns = [
    { header: 'ID', render: (r) => <span style={{ color: 'var(--text-muted)', fontFamily: 'monospace' }}>#{r.userId}</span> },
    {
      header: 'Full Name',
      render: (r) => {
        const name = `${r.firstName || ''} ${r.lastName || ''}`.trim() || 'N/A';
        const roleName = String(r.roleName || '').replace('ROLE_', '').toUpperCase();
        const meta = ROLE_COLORS[roleName] || {};
        return (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <span style={{ fontSize: '1.1rem' }}>{meta.icon || '👤'}</span>
            <strong style={{ color: 'var(--text-primary)' }}>{name}</strong>
          </div>
        );
      }
    },
    { header: 'Email', render: (r) => <span style={{ color: 'var(--text-secondary)', fontSize: '0.85rem' }}>{r.email}</span> },
    { header: 'Phone', render: (r) => r.phone || '—' },
    {
      header: 'Community',
      render: (r) => {
        const info = userCommunityMap[r.userId];
        const roleName = String(r.roleName || '').replace('ROLE_', '').toUpperCase();
        if (roleName === 'SUPER_ADMIN') return <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>Platform-wide</span>;
        if (!info) return <span style={{ color: 'var(--text-muted)', fontSize: '0.8rem' }}>—</span>;
        return (
          <span style={{ background: 'rgba(99,102,241,0.12)', color: '#818cf8', fontSize: '0.8rem', padding: '2px 8px', borderRadius: '6px', fontWeight: 600 }}>
            🏘️ {info.communityName}
          </span>
        );
      }
    },
    {
      header: 'Role',
      render: (r) => {
        const roleName = String(r.roleName || '').replace('ROLE_', '').toUpperCase();
        const meta = ROLE_COLORS[roleName] || { color: '#6b7280' };
        const variant = roleName.includes('ADMIN') ? 'danger' : roleName.includes('SECURITY') ? 'warning' : roleName.includes('STAFF') ? 'success' : 'info';
        return <Badge text={roleName.replace('_', ' ')} variant={variant} />;
      }
    },
    {
      header: 'Status',
      render: (r) => {
        const s = String(r.status || 'ACTIVE').toUpperCase();
        return <Badge text={s} variant={s === 'ACTIVE' ? 'success' : 'danger'} />;
      }
    }
  ];

  return (
    <DashboardLayout>
      <PageHeader
        title="Platform User Management"
        subtitle="Manage and provision accounts across all communities and roles"
        action={<button className="btn btn-primary" onClick={() => { setShowModal(true); setError(''); setSuccess(''); }}>+ Provision New User</button>}
      />

      {/* ─── Role Stats Row ───────────────────────────────────────────── */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(5, 1fr)', gap: '12px', marginBottom: '24px' }}>
        {roleStats.map(s => (
          <button
            key={s.role}
            onClick={() => setFilterRole(filterRole === s.role ? 'ALL' : s.role)}
            style={{
              background: filterRole === s.role ? `${s.color}22` : 'var(--bg-card)',
              border: `1px solid ${filterRole === s.role ? s.color : 'var(--border)'}`,
              borderRadius: '12px',
              padding: '14px',
              cursor: 'pointer',
              textAlign: 'center',
              transition: 'all 0.15s',
            }}
          >
            <div style={{ fontSize: '1.3rem' }}>{s.icon}</div>
            <div style={{ fontSize: '1.6rem', fontWeight: 900, color: s.color }}>{s.count}</div>
            <div style={{ fontSize: '0.7rem', color: 'var(--text-muted)', fontWeight: 600, textTransform: 'uppercase' }}>{s.role.replace('_', ' ')}</div>
          </button>
        ))}
      </div>

      {/* ─── Filters Row ───────────────────────────────────────────────── */}
      <div style={{ display: 'flex', gap: '12px', marginBottom: '20px', alignItems: 'center' }}>
        <input
          type="text"
          placeholder="🔍 Search by name, email or phone..."
          value={searchText}
          onChange={e => setSearchText(e.target.value)}
          style={{
            flex: 1,
            background: 'var(--bg-card)',
            border: '1px solid var(--border)',
            borderRadius: '8px',
            padding: '8px 14px',
            color: 'var(--text-primary)',
            fontSize: '0.85rem'
          }}
        />

        <select
          value={filterRole}
          onChange={e => setFilterRole(e.target.value)}
          style={{
            background: 'var(--bg-card)',
            border: '1px solid var(--border)',
            borderRadius: '8px',
            padding: '8px 14px',
            color: 'var(--text-primary)',
            fontSize: '0.85rem',
            cursor: 'pointer'
          }}
        >
          <option value="ALL">All Roles</option>
          <option value="SUPER_ADMIN">👑 Super Admin</option>
          <option value="ADMIN">🛡️ Admin</option>
          <option value="RESIDENT">🏠 Resident</option>
          <option value="SECURITY">🔒 Security</option>
          <option value="STAFF">🔧 Staff</option>
        </select>

        <select
          value={filterCommunity}
          onChange={e => setFilterCommunity(e.target.value)}
          style={{
            background: 'var(--bg-card)',
            border: '1px solid var(--border)',
            borderRadius: '8px',
            padding: '8px 14px',
            color: 'var(--text-primary)',
            fontSize: '0.85rem',
            cursor: 'pointer'
          }}
        >
          <option value="ALL">All Communities</option>
          {communities.map(c => (
            <option key={c.communityId || c.id} value={c.communityId || c.id}>
              🏘️ {c.communityName || c.name}
            </option>
          ))}
        </select>

        {(filterRole !== 'ALL' || filterCommunity !== 'ALL' || searchText) && (
          <button
            className="btn btn-secondary"
            style={{ fontSize: '0.82rem', whiteSpace: 'nowrap' }}
            onClick={() => { setFilterRole('ALL'); setFilterCommunity('ALL'); setSearchText(''); }}
          >
            ✕ Clear Filters
          </button>
        )}
      </div>

      {/* ─── Count banner ───────────────────────────────────────────────── */}
      <div style={{ marginBottom: '12px', fontSize: '0.82rem', color: 'var(--text-muted)' }}>
        Showing <strong style={{ color: 'var(--text-primary)' }}>{filteredUsers.length}</strong> of {users.length} users
        {filterRole !== 'ALL' && ` · Role: ${filterRole}`}
        {filterCommunity !== 'ALL' && ` · Community: ${communities.find(c => String(c.communityId || c.id) === filterCommunity)?.communityName || ''}`}
      </div>

      {loading ? <LoadingSpinner /> : (
        <Table columns={columns} data={filteredUsers} emptyMessage="No users found matching filters" />
      )}

      {/* ─── Provision Modal ────────────────────────────────────────────── */}
      <Modal isOpen={showModal} onClose={() => setShowModal(false)} title="Provision New Account">
        {error && <div style={{ background: 'rgba(244,63,94,0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '16px' }}>{error}</div>}
        {success && <div style={{ background: 'rgba(16,185,129,0.15)', color: 'var(--success)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '16px' }}>{success}</div>}

        <form onSubmit={handleCreateUser}>
          <FormInput
            label="Target Role *"
            type="select"
            options={roles.map(r => ({
              label: `${ROLE_COLORS[r.roleName?.replace('ROLE_', '')]?.icon || '👤'} ${r.roleName?.replace('ROLE_', '').replace('_', ' ') || r.name}`,
              value: r.roleId || r.id
            }))}
            value={formData.roleId}
            onChange={e => setFormData({ ...formData, roleId: Number(e.target.value) })}
            required
          />

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
            <FormInput label="First Name *" value={formData.firstName} onChange={e => setFormData({ ...formData, firstName: e.target.value })} placeholder="Raj" required />
            <FormInput label="Last Name *" value={formData.lastName} onChange={e => setFormData({ ...formData, lastName: e.target.value })} placeholder="Kumar" required />
          </div>
          <FormInput label="Email Address *" type="email" value={formData.email} onChange={e => setFormData({ ...formData, email: e.target.value })} placeholder="user@example.com" required />
          <FormInput label="Phone Number *" value={formData.phone} onChange={e => setFormData({ ...formData, phone: e.target.value })} placeholder="9876543210" required />
          <FormInput label="Initial Password *" type="password" value={formData.password} onChange={e => setFormData({ ...formData, password: e.target.value })} placeholder="Min 8 characters" required />

          <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '12px', marginTop: '20px' }}>
            <button type="button" className="btn btn-secondary" onClick={() => setShowModal(false)}>Cancel</button>
            <button type="submit" className="btn btn-primary" disabled={saving}>
              {saving ? 'Creating...' : 'Create Account'}
            </button>
          </div>
        </form>
      </Modal>
    </DashboardLayout>
  );
}
