import { Bell, LogOut, User } from 'lucide-react';
import { useAuth } from '../../auth/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();

  return (
    <header className="navbar">
      <div className="navbar-left">
        <h3 style={{ fontSize: '1rem', fontWeight: 600, color: 'var(--text-muted)' }}>
          UrbanLife Community OS
        </h3>
      </div>
      <div className="navbar-user">
        <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
          <User size={18} color="var(--primary)" />
          <span style={{ fontSize: '0.9rem', fontWeight: 500 }}>{user?.email}</span>
        </div>
        <span className="user-badge">{user?.role?.replace('ROLE_', '')}</span>
        
        <button 
          onClick={logout} 
          className="btn btn-secondary" 
          style={{ padding: '6px 12px', fontSize: '0.8rem' }}
          title="Logout"
        >
          <LogOut size={14} /> Logout
        </button>
      </div>
    </header>
  );
}
