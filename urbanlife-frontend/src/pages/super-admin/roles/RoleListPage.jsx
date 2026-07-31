import { useEffect, useState } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import { roleApi } from '../../../api/roleApi';

import Badge from '../../../components/common/Badge';

export default function RoleListPage() {
  const [roles, setRoles] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    roleApi.getAll()
      .then(res => setRoles(res.data))
      .catch(err => console.error(err))
      .finally(() => setLoading(false));
  }, []);

  const columns = [
    { header: 'ID', render: (r) => r.roleId || r.id },
    { header: 'Role Code', render: (r) => <Badge text={r.roleName || r.name} /> },
    { header: 'Security Authority Level', render: (r) => `Platform ${r.roleName || r.name} authorization access level` }
  ];

  return (
    <DashboardLayout>
      <PageHeader title="Role Management" subtitle="Platform roles and security authority matrix" />
      {loading ? <LoadingSpinner /> : <Table columns={columns} data={roles} />}
    </DashboardLayout>
  );
}
