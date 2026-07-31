export const getDashboardRoute = (role) => {
  const cleanRole = role?.replace('ROLE_', '');
  switch (cleanRole) {
    case 'SUPER_ADMIN':
      return '/super-admin/dashboard';
    case 'ADMIN':
      return '/admin/dashboard';
    case 'RESIDENT':
      return '/resident/dashboard';
    case 'SECURITY':
      return '/security/dashboard';
    case 'STAFF':
      return '/staff/dashboard';
    default:
      return '/login';
  }
};
