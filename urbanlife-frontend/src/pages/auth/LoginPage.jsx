import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import AuthLayout from '../../components/layout/AuthLayout';
import FormInput from '../../components/common/FormInput';
import { useAuth } from '../../auth/AuthContext';
import { userApi } from '../../api/userApi';
import { residentApi } from '../../api/residentApi';
import { getDashboardRoute } from '../../utils/roleUtils';
import { ShieldCheck } from 'lucide-react';

export default function LoginPage() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  const { login } = useAuth();
  const navigate = useNavigate();

  const handleLogin = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');

    try {
      // Temporarily store credentials for basic auth interceptor
      localStorage.setItem('email', email);
      localStorage.setItem('password', password);

      // Verify user identity against Spring Boot backend
      const res = await userApi.getByEmail(email);
      const userData = res.data;

      // Extract roleName from UserResponse (e.g. "SUPER_ADMIN", "ADMIN", "RESIDENT", etc.)
      const rawRole = userData.roleName || (userData.roles && userData.roles[0]?.name) || (userData.role && userData.role.roleName) || 'RESIDENT';
      const userRole = String(rawRole).replace('ROLE_', '');
      const userId = userData.userId || userData.id;

      // For resident/security/staff roles: resolve communityId from their resident profile
      let communityId = userData.communityId || null;
      if (!communityId && ['RESIDENT', 'SECURITY', 'STAFF'].includes(userRole)) {
        try {
          const rRes = await residentApi.getByUserId(userId);
          const residentData = rRes.data;
          // residentData has communityId or community.communityId
          communityId = residentData?.communityId
            || residentData?.community?.communityId
            || residentData?.community?.id
            || null;
        } catch (e) { /* Not a resident profile — skip */ }
      }

      login(email, password, userRole, userId, communityId);
      navigate(getDashboardRoute(userRole));
    } catch (err) {
      localStorage.clear();
      setError('Invalid email or password. Please check your credentials.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout>
      <div style={{ textAlign: 'center', marginBottom: '24px' }}>
        <div style={{
          width: '48px',
          height: '48px',
          background: 'var(--primary-light)',
          color: 'var(--primary)',
          borderRadius: '12px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          margin: '0 auto 12px auto'
        }}>
          <ShieldCheck size={28} />
        </div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>UrbanLife Portal</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Sign in to manage your community</p>
      </div>

      {error && (
        <div style={{ background: 'rgba(244, 63, 94, 0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '16px' }}>
          {error}
        </div>
      )}

      <form onSubmit={handleLogin}>
        <FormInput
          label="Email Address"
          type="email"
          name="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          placeholder="e.g. admin@urbanlife.com"
          required
        />

        <FormInput
          label="Password"
          type="password"
          name="password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          placeholder="••••••••"
          required
        />

        <button
          type="submit"
          className="btn btn-primary"
          style={{ width: '100%', marginTop: '8px', padding: '12px' }}
          disabled={loading}
        >
          {loading ? 'Authenticating...' : 'Sign In'}
        </button>
      </form>

      <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
        Don't have an account? <Link to="/register" style={{ color: 'var(--primary)', fontWeight: 600 }}>Create Account</Link>
      </div>
    </AuthLayout>
  );
}
