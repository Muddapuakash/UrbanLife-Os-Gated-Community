export default function AuthLayout({ children }) {
  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      backgroundColor: 'var(--bg-main)',
      padding: '20px'
    }}>
      <div className="card" style={{ width: '100%', maxWidth: '420px', padding: '32px' }}>
        {children}
      </div>
    </div>
  );
}
