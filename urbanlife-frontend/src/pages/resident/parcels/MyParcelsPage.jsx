import { useEffect, useState, useCallback } from 'react';
import DashboardLayout from '../../../components/layout/DashboardLayout';
import PageHeader from '../../../components/common/PageHeader';
import Table from '../../../components/common/Table';
import Badge from '../../../components/common/Badge';
import LoadingSpinner from '../../../components/common/LoadingSpinner';
import { parcelApi } from '../../../api/parcelApi';
import { residentApi } from '../../../api/residentApi';
import { formatDate } from '../../../utils/formatDate';

const REFRESH_INTERVAL = 8000; // 8 seconds live refresh

export default function MyParcelsPage() {
  const [parcels, setParcels] = useState([]);
  const [loading, setLoading] = useState(true);
  const [lastUpdated, setLastUpdated] = useState(null);
  const [notMapped, setNotMapped] = useState(false);

  const currentUserId = Number(localStorage.getItem('userId')) || 1;

  const loadParcels = useCallback((residentId, flatId) => {
    // Try by residentId first; fall back to flatId
    const req = residentId
      ? parcelApi.getByResident(residentId)
      : flatId
        ? parcelApi.getByFlat(flatId)
        : null;

    if (!req) {
      setParcels([]);
      setLoading(false);
      return;
    }

    req
      .then(res => {
        setParcels(res.data || []);
        setLastUpdated(new Date());
      })
      .catch(() => {
        // If by resident fails, try by flat
        if (residentId && flatId) {
          parcelApi.getByFlat(flatId)
            .then(r => setParcels(r.data || []))
            .catch(() => setParcels([]));
        } else {
          setParcels([]);
        }
      })
      .finally(() => setLoading(false));
  }, []);

  useEffect(() => {
    let interval;

    residentApi.getByUserId(currentUserId)
      .then(res => {
        const r = res.data;
        const residentId = r?.residentId || r?.id;
        const flatId = r?.flatId;

        if (!residentId && !flatId) {
          setNotMapped(true);
          setLoading(false);
          return;
        }

        loadParcels(residentId, flatId);
        interval = setInterval(() => loadParcels(residentId, flatId), REFRESH_INTERVAL);
      })
      .catch(() => {
        setNotMapped(true);
        setLoading(false);
      });

    return () => { if (interval) clearInterval(interval); };
  }, [currentUserId, loadParcels]);

  const copyCode = (code) => {
    navigator.clipboard.writeText(code).then(() => alert(`📋 Copied: ${code}`));
  };

  const columns = [
    {
      header: 'Pickup Code (Show at Gate)',
      render: (r) => {
        const code = r.pickupCode || r.code || 'N/A';
        return (
          <div style={{ display: 'flex', alignItems: 'center', gap: '8px' }}>
            <strong style={{
              color: 'var(--primary)', fontFamily: 'monospace',
              fontSize: '1rem', letterSpacing: '1px'
            }}>
              {code}
            </strong>
            {code !== 'N/A' && (
              <button
                onClick={() => copyCode(code)}
                style={{
                  background: 'none', border: '1px solid var(--border)',
                  borderRadius: '4px', padding: '2px 6px',
                  cursor: 'pointer', fontSize: '0.7rem', color: 'var(--text-muted)'
                }}
              >
                📋 Copy
              </button>
            )}
          </div>
        );
      }
    },
    { header: 'Delivery Provider', render: (r) => r.deliveryProvider || 'N/A' },
    { header: 'Package Type', render: (r) => r.parcelType || 'N/A' },
    { header: 'Received At', render: (r) => formatDate(r.createdAt || r.receivedAt) },
    { header: 'Status', render: (r) => <Badge text={r.status} /> }
  ];

  return (
    <DashboardLayout>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: '4px' }}>
        <PageHeader
          title="Deliveries & Parcels"
          subtitle="Packages received at gate and waiting for pickup at the security desk"
        />
        <div style={{
          fontSize: '0.75rem', color: 'var(--text-muted)',
          display: 'flex', alignItems: 'center', gap: '6px',
          paddingTop: '8px', whiteSpace: 'nowrap'
        }}>
          <span style={{
            width: '8px', height: '8px', borderRadius: '50%',
            background: 'var(--success)', display: 'inline-block'
          }}></span>
          Live · {lastUpdated ? lastUpdated.toLocaleTimeString('en-IN') : '...'}
        </div>
      </div>

      {notMapped && (
        <div style={{
          marginBottom: '20px', padding: '14px 18px',
          background: 'rgba(239,68,68,0.1)', borderLeft: '4px solid var(--danger)', borderRadius: '8px'
        }}>
          <strong style={{ color: 'var(--danger)' }}>⚠️ Flat Assignment Required:</strong>
          <span style={{ fontSize: '0.88rem', color: 'var(--text-muted)', marginLeft: '8px' }}>
            Your account is not linked to a flat yet. Ask your Society Admin to map you in Admin → Resident Directory.
          </span>
        </div>
      )}

      {loading
        ? <LoadingSpinner />
        : <Table
            columns={columns}
            data={parcels}
            emptyMessage="No packages logged for your flat at the gate. Parcels will appear here once logged by security."
          />
      }
    </DashboardLayout>
  );
}
