import { Navigate } from 'react-router-dom';
import { useAuth } from './AuthContext';

export default function ProtectedRoute({ allowedRoles, children }) {
  const { user } = useAuth();

  if (!user || !user.email) {
    return <Navigate to="/login" replace />;
  }

  const cleanUserRole = user.role?.replace('ROLE_', '');
  const cleanAllowed = allowedRoles.map(r => r.replace('ROLE_', ''));

  if (!cleanAllowed.includes(cleanUserRole)) {
    return <Navigate to="/unauthorized" replace />;
  }

  return children;
}
