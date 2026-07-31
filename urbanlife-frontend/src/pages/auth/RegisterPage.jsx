import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import AuthLayout from '../../components/layout/AuthLayout';
import FormInput from '../../components/common/FormInput';
import { userApi } from '../../api/userApi';
import { UserPlus, ShieldCheck } from 'lucide-react';

export default function RegisterPage() {
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    password: '',
    roleId: 3 // Default: RESIDENT (3)
  });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  const navigate = useNavigate();

  const handleRegister = async (e) => {
    e.preventDefault();
    setLoading(true);
    setError('');
    setSuccess('');

    try {
      await userApi.register(formData);
      setSuccess('Account created successfully! Redirecting to login...');
      setTimeout(() => {
        navigate('/login');
      }, 1500);
    } catch (err) {
      setError(err.response?.data?.message || 'Registration failed. Check your inputs or try another email.');
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
          <UserPlus size={28} />
        </div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800 }}>Create Account</h2>
        <p style={{ fontSize: '0.85rem', color: 'var(--text-muted)' }}>Register for UrbanLife Community OS</p>
      </div>

      {error && (
        <div style={{ background: 'rgba(244, 63, 94, 0.15)', color: 'var(--danger)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '16px' }}>
          {error}
        </div>
      )}

      {success && (
        <div style={{ background: 'rgba(16, 185, 129, 0.15)', color: 'var(--success)', padding: '10px 14px', borderRadius: '8px', fontSize: '0.85rem', marginBottom: '16px' }}>
          {success}
        </div>
      )}

      <form onSubmit={handleRegister}>
        <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '12px' }}>
          <FormInput
            label="First Name"
            name="firstName"
            value={formData.firstName}
            onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
            placeholder="John"
            required
          />
          <FormInput
            label="Last Name"
            name="lastName"
            value={formData.lastName}
            onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
            placeholder="Doe"
            required
          />
        </div>

        <FormInput
          label="Email Address"
          type="email"
          name="email"
          value={formData.email}
          onChange={(e) => setFormData({ ...formData, email: e.target.value })}
          placeholder="john@example.com"
          required
        />

        <FormInput
          label="Phone Number"
          name="phone"
          value={formData.phone}
          onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
          placeholder="9876543210"
          required
        />

        <div style={{
          background: 'rgba(16, 185, 129, 0.08)',
          border: '1px solid rgba(16, 185, 129, 0.25)',
          borderRadius: '10px',
          padding: '12px 14px',
          marginBottom: '16px',
          display: 'flex',
          alignItems: 'center',
          gap: '10px'
        }}>
          <div style={{
            background: 'var(--primary-light)',
            color: 'var(--primary)',
            padding: '6px',
            borderRadius: '6px',
            display: 'flex'
          }}>
            <ShieldCheck size={20} />
          </div>
          <div>
            <div style={{ fontSize: '0.85rem', fontWeight: 700, color: 'var(--primary)' }}>Resident Self-Registration</div>
            <div style={{ fontSize: '0.78rem', color: 'var(--text-muted)' }}>
              Security Guards, Staff & Admin accounts are provisioned by Society Management.
            </div>
          </div>
        </div>

        <FormInput
          label="Password"
          type="password"
          name="password"
          value={formData.password}
          onChange={(e) => setFormData({ ...formData, password: e.target.value })}
          placeholder="••••••••"
          required
        />

        <button
          type="submit"
          className="btn btn-primary"
          style={{ width: '100%', marginTop: '8px', padding: '12px' }}
          disabled={loading}
        >
          {loading ? 'Creating Account...' : 'Register'}
        </button>
      </form>

      <div style={{ textAlign: 'center', marginTop: '20px', fontSize: '0.85rem', color: 'var(--text-muted)' }}>
        Already have an account? <Link to="/login" style={{ color: 'var(--primary)', fontWeight: 600 }}>Sign In</Link>
      </div>
    </AuthLayout>
  );
}
