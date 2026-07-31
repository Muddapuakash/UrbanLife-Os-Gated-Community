export default function PageHeader({ title, subtitle, action }) {
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '24px' }}>
      <div>
        <h2 style={{ fontSize: '1.5rem', fontWeight: 800, color: 'var(--text-main)' }}>{title}</h2>
        {subtitle && <p style={{ fontSize: '0.875rem', color: 'var(--text-muted)' }}>{subtitle}</p>}
      </div>
      {action && <div>{action}</div>}
    </div>
  );
}
